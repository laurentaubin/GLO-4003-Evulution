# Contributing

When contributing to this repository, please first discuss the changes you wish to make by creating an issue.

## Work flow
We chose to use a hybrid between [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) and [Trunk based development](https://www.atlassian.com/continuous-delivery/continuous-integration/trunk-based-development). In our work flow, there are three types of branches:

### Branches

#### Main
This is the production branch. Everything contained in this branch should be production ready and ready to be deployed. Once a PR is merged into the `main` branch, a deployment to production is expected.

#### Release
Every user story defined by the project owner should have its corresponding `release` branch. These branches are merged into the `main` branch once all of the tasks related to the story are done.

#### Feature
Feature branches are used as development branches. They should roughly have a corresponding issue in the project's backlog. Feature branches are merged into their corresponding `release` branch once the required work to complete the issue has been done.

### Epics

We refer to epics by their tags. Below is a mapping of every epic and its respective tag.
| Epic number | Name      | Name abbreviation |
|-------------|-----------|-------------------|
| Epic #1     | Jonathan  | JON               |
| Epic #2     | Antoine   | ANT               |
| Epic #3     | Catherine | CAT               |
| Epic #4     | Guy       | GUY               |

## Styleguides

### Pull request (PR) naming
- The title of the PR should be self-explanatory
- Put the `WIP:` tag before the name of your PR if it is in progress
- Put the issue tag followed by the user story number at the beginning of the pull request, like `JON-6`
- After you submit your pull request, verify that all status checks are passing
- When merging, squash all commits into one. The commit message should be the same as the PR name

For example:
`WIP: [JON-6] Add user registration`

### Branch naming
Branch names should be written in kebab-case

#### Release branches
Release branches' names should be composed of two parts:
- The word "release"
- Story tag

For example:
`release/JON-6`

#### Feature branches
Feature branches' names should be composed of three parts:
- Applicable word
    - "feat" when the adding a new feature
    - "fix" when fixing a bug
    - "refactor" when refactoring
- Story tag
- Name of the issue

For example:
`feat/JON-6/add-user-registration`

### Git commit message
- Use the present tense
- Use the imperative mood
- The commit message needs to be descriptive

For example:
`git commit -m "Update contributing.md"`

### Code style

All code is written using [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

It is recommended to install the `Save Actions` plugin, to format your file on save. If you use the plugin, enable:
- Activate save actions on save
- Optimize imports
- Reformat file

### Documentation Styleguide
- Use [Markdown](https://www.markdownguide.org/basic-syntax/)

### Testing culture

Everything that might break must be tested.

You must try to use Test Driven Development (TDD)

This project uses [JUnit5](https://junit.org/junit5/).

Please, follow this list when creating a test
- Tests must respect the `given_when_then` naming convention
- A test should be fast
- A test should be automated
- A test should be independent of the environment, time, connection, etc.
- A test must have only one reason to fail
- The code must respect the same standards of quality that apply to production code
