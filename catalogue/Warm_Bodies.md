[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Warm Bodies


# Warm Bodies

**Also Known As:** Mythical Man-Month


## Summary

Software project is staffed with too many programmers, many of which (the warm bodies) have inadequate skills and productivity levels and were hired perhaps to meet staff size objectives or as a "solution" to speed-up a late project.  Exceptionally productive programmers become frustrated, partly because they spend time fixing the damage of the warm bodies.


## Symptoms

 - team size significantly larger than a one-digit number (4 is suggested in [CUN'13])
 - overall programmer productivity (LOC commited or similar) low, with a few exceptions
 - time spent on meetings, mentoring, helping out activities significant (in comparison to time on engineering activities)
 - a few programmers -- those with the high productivity -- contributing bugfixes, code corrections and similar to code originally comitted by other programmers

## Specific To

any

## Related Anti-patterns

|Anti-pattern  | Relation |
|--|--|
| [Net Negative Producing Programmer](Net_Negative_Producing_Programmer.md) | extreme case of a warm body |
| Never Fire Anyone ([C2 wiki](http://wiki.c2.com/?NeverFireAnyone) | one source of warm bodies |
| [Brooks' Law](Brooks_Law.md) | one source of warm bodies |

## Notes

The description in [CUN'13] and the full mini-AntiPattern description in [BRO'98] (taken from there) is rather unclear, focuses on staff size and differences in programmer productivity, does not explain what "warm body" actually is.  The NeverFireAnyone anti-pattern in [CUN'13] actually explains this, to some extent.

## Sources

[[CUN'13]](../References.md)
