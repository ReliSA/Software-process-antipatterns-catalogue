[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Architects Don't Code
# Architects Don't Code
**Also Known As:** (pattern "Architect Also Implements")
## Summary
System architects do not participate in the development efforts, e.g. because their time is expensive. Thus they ultimately create designs just "on paper" which might be flawed but which the developers are supposed to follow, or are not able to estimate/understand how their changes to the design affect the project.
## Symptoms
 - people with the architect role do not interact with coding tasks (tickets)
 - architects do not generate or modify any source code artifacts
 - architects only interact with non-coding people, tasks (tickets) and artifacts
 - (maybe?) implementation lags, or large refactorings are performed late in the project, or bugs on non-functional properties (e.g. performance) are reported after system is deployed to production
## Specific To
More likely in waterfall projects.
## Related Anti-patterns
| Anti-pattern  | Relation |
|--|--|
| [Viewgraph Engineering](Viewgraph_Engineering.md) | similar in kind (technical role does not get hands dirty in technical tasks) |
## Sources
* [[CUN'10]](../References.md) [ArchitectsDontCode](http://wiki.c2.com/?ArchitectsDontCode)
* F.Brooks: The Second-System Effect.  In: The Mythical Man-Month, 20th Anniversary Edition, Addison-Wesley 1995. ISBN 0-201-83595-9
