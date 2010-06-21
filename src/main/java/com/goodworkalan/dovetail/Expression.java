package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.DovetailException.FORMAT_PARAMETER_IS_NULL;
import static com.goodworkalan.dovetail.DovetailException.MISMATCHED_IDENTIFIERS;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An expression that matches a part or sub path in a path pattern.
 * 
 * @author Alan Gutierrez
 */
final class Expression implements Test {
    /**
     * The identifiers used to reference regular expression capture groups in
     * group order.
     */
    private final List<String> identifiers;
    
    /** The regular expression. */
    private final Pattern regex;

    /**
     * The format used to create the path part when creating generating the path
     * from a parameter map.
     */
    private final String sprintf;
    
    /** The minimum number of path parts this expression can match. */
    private final int min;
    
    /** The maximum number of path parts this expression can match. */
    private final int max;
    
    /** Whether this . */
    private final boolean multiple;
    
    // TODO Document.
    public Expression(List<String> identifiers, Pattern regex, String sprintf, int min, int max, boolean deep) {
        this.identifiers = identifiers;
        this.regex = regex;
        this.sprintf = sprintf;
        this.min = min;
        this.max = max;
        this.multiple = deep;
    }

    // TODO Document.
    public boolean match(Map<String, String> parameters, String[] parts, int start, int end) {
        if (min == 0 && end - start == 0) {
            return true;
        }
        if (multiple) {
            StringBuilder path = new StringBuilder();
            for (int i = start; i < end; i++) {
                path.append(parts[i]).append("/");
            }
            Matcher matcher = regex.matcher(path);
            if (matcher.matches()) {
                parameters(matcher, parameters);
                return true;
            }

        } else if (end - start != 1) {
            throw new IllegalStateException();
        } else {
            Matcher matcher = regex.matcher(parts[start]);
            if (matcher.matches()) {
                parameters(matcher, parameters);
                return true;
            }
        }
        return false;
    }

    // TODO Document.
    private boolean parameters(Matcher matcher, Map<String, String> parameters) {
        if (matcher.matches()) {
            if (matcher.groupCount() == 0) {
                if (identifiers.size() != 1) {
                    throw new DovetailException(MISMATCHED_IDENTIFIERS).add(1, identifiers.size());
                }
                parameters.put(identifiers.get(0), matcher.group());
            } else {
                if (identifiers.size() != matcher.groupCount()) {
                    throw new DovetailException(MISMATCHED_IDENTIFIERS).add(matcher.groupCount(), identifiers.size());
                }
                for (int i = 0; i < matcher.groupCount(); i++) {
                    parameters.put(identifiers.get(i), matcher.group(i + 1));
                }
            }
            return true;
        }
        return false;
    }

    // TODO Document.
    public int getMin() {
        return min;
    }

    // TODO Document.
    public int getMax() {
        return max;
    }
    
    // TODO Document.
    public void append(StringBuilder path, Map<String, String> parameters) {
        Object[] args = new Object[identifiers.size()];
        for (int i = 0; i < identifiers.size(); i++) {
            args[i] = parameters.get(identifiers.get(i));
            if (args[i] == null) {
                throw new DovetailException(FORMAT_PARAMETER_IS_NULL).add(identifiers.get(i));
            }
        }
        path.append(String.format(sprintf, args));
    }

    // TODO Document.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Expression) {
            Expression other = (Expression) obj;
            return min == other.min && max == other.max
                    && identifiers.equals(other.identifiers)
                    && regex.pattern().equals(other.regex.pattern())
                    && sprintf.equals(other.sprintf)
                    && multiple == other.multiple;
        }
        return false;
    }

    // TODO Document.
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 37 + max;
        hash = hash * 37 + min;
        hash = hash * 37 + identifiers.hashCode();
        hash = hash * 37 + regex.pattern().hashCode();
        hash = hash * 37 + sprintf.hashCode();
        hash = hash * 37 + (multiple ? 15485867 : 32452843);
        return hash;
    }

    /**
     * Return the expression pattern.
     * 
     * @return The expression pattern.
     */
    @Override
    public String toString() {
        StringBuilder ids = new StringBuilder();
        String separator = "";
        for (String id : identifiers) {
            ids.append(separator).append(id);
        }
        return "{identifiers: " + ids + ", regex: " + regex.pattern() + ", format: " + sprintf + "}";
    }
}