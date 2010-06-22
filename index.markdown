---
layout: default
title: Dovetail
---

# Dovetail Concerns and Decisions

## Disfavor Immutability

Got rid of `Globber`. Which would have ment that the matcher itself was
immutable, done by spliting the interface into object and builder, which if done
only for the sake of immutability, instead of for the sake of simplifying the
construction of a complex object. I disfavor. Instead, I'll provide a copy
constructor for the sake of immutability, or rather sharing the information with
another thread. (Someone needs to make these rules much simpiler to understand.)

Now the real patterns are beginning to reveal themselves. The `PathTree` is
really a `PathAssociation` and the tree nature is an optimization that is an
implementation detail, one that can described in documentation, but not in the
name of the class.

http://github.com/bigeasy/dovetail/commit/f9b7fb0eb47176228ec66c59a2320dff5b44ecc1

## Namespace

Currently using the name `Glob`, which is incorrect. A Glob matches many files
and globs them together. Dovetail is a pattern matcher for URL patterns. The
correct word is neither `Pattern` nor `Matcher`, which are already taken anyway.

Borrowing from the namespace, I'm thinking about renaming `Glob` to `Jig`.

## Match Filtering

The notion of futher reducing a match set through a match test interface needs
to be revisited. This may have been relevant to Stripes, when everything needed
to be crammed into an annotation, but it is not necessary for Paste. We can move
the filtering out of Dovetail and into Paste, snice it is Paste specific.

## Multiple

Currently, there is a flag for multiple, but couldn't we just say, if you match
more than one, you will get the special case. Really, no. It should always be
dictated by the format string, so that [1,1] is multiple.
