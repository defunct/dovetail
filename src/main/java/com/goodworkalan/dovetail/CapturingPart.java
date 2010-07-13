package com.goodworkalan.dovetail;

import static com.goodworkalan.dovetail.Path.FORMAT_PARAMETER_IS_NULL;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.goodworkalan.danger.Danger;

/**
 * An expression that matches a part or sub path in a path pattern.
 * 
 * @author Alan Gutierrez
 */
final class CapturingPart implements Part {
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
    
    /** Whether this capturing part matches multiple parts or a single part. */
    private final boolean multiple;

    /**
     * Create a new capturing part.
     * 
     * @param identifiers
     *            The identifiers used to reference regular expression capture
     *            groups in group order.
     * @param regex
     *            The regular expression.
     * @param sprintf
     *            The format used to create the path part when creating
     *            generating the path from a parameter map.
     * @param min
     *            The minimum number of path parts this expression can match.
     * @param max
     *            The maximum number of path parts this expression can match.
     * @param multiple
     *            Whether this capturing part matches multiple parts or a single
     *            part.
     */
    public CapturingPart(List<String> identifiers, Pattern regex, String sprintf, int min, int max, boolean multiple) {
        this.identifiers = identifiers;
        this.regex = regex;
        this.sprintf = sprintf;
        this.min = min;
        this.max = max;
        this.multiple = multiple;
    }

    /**
     * Return true if the capturing part matches the parts from the given start
     * index to the given end index.
     * <p>
     * If the multiple flag is false, the regular expression is applied against
     * a single part, and if the range denotes any count of parts other than 1,
     * an <code>IllegalArgumentException</code> is raised.
     * <p>
     * If this the multiple flag is true, the regular expression is applied
     * against a catenated string of the parts, slash separated, with a trailing
     * slash to simply regular expression authoring.
     * 
     * @param parameters
     *            The map of captured parameters.
     * @param parts
     *            The split path parts.
     * @param start
     *            The start index.
     * @param end
     *            The end index.
     * @return True if this capturing part matches the given range of parts.
     */
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

    /**
     * Populate the given map of parameters with the parameters captured by the
     * regular expression matcher, if the regular expression matcher is
     * successful.
     * 
     * @param matcher
     *            The regular expression matcher.
     * @param parameters
     *            The map of parameters to populate.
     * @return True of the regular expression matcher is successful.
     */
    private boolean parameters(Matcher matcher, Map<String, String> parameters) {
        if (matcher.matches()) {
            if (matcher.groupCount() == 0) {
                parameters.put(identifiers.get(0), matcher.group());
            } else {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    parameters.put(identifiers.get(i), matcher.group(i + 1));
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Get the minimum number of path parts this part expression can match.
     * 
     * @return The minimum number of parts this part expression can match.
     */
    public int getMin() {
        return min;
    }

    /**
     * Get the maximum number of path parts this part expression can match.
     * 
     * @return The maximum number of parts this part expression can match.
     */
    public int getMax() {
        return max;
    }

    /**
     * Assemble a path by appending the capturing part text to the given string
     * builder substituting values from the given parameter map for the capture
     * placeholders.
     * 
     * @param path
     *            The path under construction.
     * @param parameters
     *            The parameters used to populate captures.
     */
    public void append(StringBuilder path, Map<String, String> parameters) {
        Object[] args = new Object[identifiers.size()];
        for (int i = 0; i < identifiers.size(); i++) {
            args[i] = parameters.get(identifiers.get(i));
            if (args[i] == null) {
                throw new Danger(Path.class, FORMAT_PARAMETER_IS_NULL, identifiers.get(i));
            }
        }
        path.append(String.format(sprintf, args));
    }

    /**
     * This object is equal to the given object if it is also a
     * <code>CapturingPart</code> and the minimum and maximum match limits, the
     * identifiers, the regular expression pattern, the reassembly sprintf
     * format, and the multiple flag of this object are equal to that of the
     * given object.
     * 
     * @param object
     *            The object to test for equality.
     * @return True if the object is equal to the given object.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof CapturingPart) {
            CapturingPart other = (CapturingPart) object;
            return min == other.min && max == other.max
                    && identifiers.equals(other.identifiers)
                    && regex.pattern().equals(other.regex.pattern())
                    && sprintf.equals(other.sprintf)
                    && multiple == other.multiple;
        }
        return false;
    }

    /**
     * Generate a hash code by combining the hash codes of minimum and maximum
     * match limits, the identifiers, the regular expression pattern, the
     * reassembly sprintf format, and the multiple flag.
     * 
     * @return The hash code.
     */
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