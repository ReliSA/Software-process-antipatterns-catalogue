[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Collective Ownership 
# Collective Ownership (as anti-pattern)
**Also Known As:**
## Summary
System modules are developed in quasi-equal measure by many developers. Therefore no-one has the responsibility for and complete understanding of the module's functionality and implementation details. This makes precise, systematic and maintainable changes harder or even impossible.
## Symptoms
 - a given larger logical piece of code (e.g., module) has no designated owner or main contributor
 - module code (+ associated artefacts, e.g. documentation) is frequently modified by several people to a similar extent
 - changes by other developers are integrated and committed but not approved, verified or documented by someone prior to the commit
 - (maybe?) tickets related to the module's functionality are assigned/resolved and-or verified/closed by several people with similar occurence ratio
## Specific To
projects using Agile methodologies
## Related Anti-patterns
## Sources
mirror anti-pattern to (violation of) the Code Ownership organizational pattern from [[COP'04]](../References.md)
