# project02_team_3
<!--
CST 338 Project 2 — README template.
Copy this file into the ROOT of your team's repository as README.md and keep it current.
This README is your project dashboard: it is the first thing the instructor reads when
grading, and a working, up-to-date README is part of your integration score.

GitHub Issues are your LIVE tracker — every slice task, enhancement, and scope decision is
an Issue: assigned to its owner, labeled (slice-1, testing, enhancement, will-not-do,
extra-credit), and closed by a PR via "Closes #N". The tables below link into those Issues
and PRs. Replace every <placeholder> and delete this comment before you submit.
-->

# <App Name>

<One-line description.> CST 338 Project 2 — Team **<Team Name>**.

## Team & Slice Ownership
| Slice | Owner | GitHub username | Issues | Branch(es)                                                 | PR(s)          | Enhancement chosen | Status |
|-------|-------|-----------------|--------|------------------------------------------------------------|----------------|--------------------|--------|
| 1 — Accounts | Miguel Quezada | mquezada2026 | #8, #9, #10, #11 | miguel/initial-commit-login-scene-skeleton, miguel/registered-users-fxml-scene | #12, #13       | TableView/ListView, custom reusable FXML component, Supabase backend (with local persistence) | in-progress|
| 2 — Catalog | Ha Nguyen | hanguyen1979 |#5, #6, #7 | Ha/catalog skeleton                                        | PR #14, #20, #22        |Notifications / Alerts | in-progress |
| 3 — Cart & Checkout | Isabel Ramirez | isramirez-jpg | #1 #2 #3 #4 | isabel/cart-database-dao, isabel/cart-clean-integration    | #15, # 18, #21 | JavaFX TableView<CartItem> | in-progress|
| 4 — Order History & Management | | | |                                                            |                | | WILL NOT DO|
| 5 - Reviews & Ratings | | | |                                                            |                | | WILL NOT DO|

_Status values: planned · in-progress · complete_

## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- Slice 4 — <name>: not building (team of 3).
- Slice 5 - <name>: not building (team of 3).
- <Slice 1>: addresses — out of scope.
- <Slice 1>: extra roles — out of scope.
- <Slice 2>: categories/filters — out of scope.
- <Slice 2>: image handling — out of scope.
- <Slice 2>: pagination — out of scope.
- <Slice 3>: coupons — out of scope.
- <Slice 3>: tax/shipping — out of scope.
- <Slice 3>: payment-API stub — out of scope.

## Code Review Log
| PR   | Author         | Human reviewer(s)               | AI review (link) | Outcome   |
|------|----------------|---------------------------------|------------------|-----------|
| #12  | Miguel Quezada | Isabel Ramirez                  | | Approved  |
| #13  | Miguel Quezada | Ha Nguyen                       | | Approved  |
| #14  | Ha Nguyen      | Miguel Quezada                  | <link or commit> | Approved  |
| #15  | Isabel Ramirez | Miguel Quezada                  | | Approved  |
| #18  | Isabel Ramirez | Ha Nguyen                       | | Approved  |
| #19  | Miguel Quezada | Isabel Ramirez                  | | Approved  |
| #20  | Ha Nguyen      | Isabel Ramirez & Miguel Quezada | | Approved  |
| #21  | Isabel Ramirez | Miguel Quezada  & Ha Nguyen     |https://claude.ai/share/40911211-0ae5-4287-8c93-4bad04e4e800 | Approved  |


## AI Usage Log

- AI-drafted tests: <link to TESTING.md / commit> — per owner.
- AI code review: [PR #21](https://github.com/isramirez-jpg/project02_team_3/pull/21) — feedback reviewed and applicable checkout error-handling and transaction-safety recommendations implemented by Isabel Ramirez.
- AI code review: [PR #22](https://github.com/isramirez-jpg/project02_team_3/pull/22) - Review PR for ha/catalog-add-product branch

## Extra Credit Log
| Item | Who | Evidence (Issue/PR) |
|------|-----|---------------------|
|  | | |

## Build & Run
```
./gradlew run        # launch the app
./gradlew test       # run the test suite
```
Requirements: JDK <version>, JavaFX <version>. Any setup notes go here.
