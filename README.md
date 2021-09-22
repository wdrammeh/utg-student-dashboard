# UTG Student Dashboard

`?`

A **flexible** and **elegant** student management system for the University of The Gambia ("UTG").

Dashboard is a **student management tool** (built by the students for the students).
This project is focus on bringing to the palms of the students (of UTG)
solutions to problems they’ve long anticipated.

A student-specific desktop application, with dedicated student-related care wizards,
that functions independently of the ERP System, and communicates with it at the student's will.

## Install

Get the standalone installation file for your operating system:

| Platform | Download Dashboard |
| ----- | ----- |
| Microsoft Windows `(msi package)` | . |
| Linux `(debian package)` | . |
| Mac iOS `(?)` | . |
| Any `(jar)` | . |

More download options (including jar builds) are available on the [release page](https://github.com/w-drammeh/utg-student-dashboard/releases).

If you wish, you may check out [what's new](ChangeLog.md) about this release.

## Functionality

- Offline content management capability
- Unlimited **Transcript Exportation** in Portable Document Format (PDF)
- News updates from UTG official news site (download once and for all)
- Explore UTG Portal
- Track semesters on the go
- Course Collection, Classification, Organization, Analysis & Presentation
- Semester to semester performance sketch
- TODO, Tasks, and Projects management
- Assignment management system with notification triggers
- UTG FAQs and Answers
- Real time performance analysis, and recommendation system
- And much more...

## System Requirements

- Firefox Web Browser

## Contribution

If you are interested in contributing to this project, firstly,
read our [Developers Guide](Contributing.md).

## ERP Notice

Dashboard uses the **UTG ERP System (Portal)** as a center of verification for users.
The Portal is Dashboard's sole source of valid data. The data collected are used for,
but not limited to, **verification**, **analysis**, and **presentation**.

It is however important to note that the analysis provided by Dashboard
is not entirely _Portal-independent_. Therefore, inconsistent data from the Portal
can induce misbehavior of your Dashboard. At every successful login, Dashboard grasps the fundamental details of the user. So students won't need to be specifying level, status on the go.

It is strongly advisable for students victim of wrong, or incomplete details
on their Portals to refer to their respective departments for help before,
or anytime soon after, launching Dashboard.

It however handles, gracefully, the common issue of missing-grades,
but cannot afford to lose core details like student's name,
or matriculation number. Dashboard may halt build, if such details
are missing, or not readable somehow.

The good news is that, all of these, if occurred, can be fixed at any point in time,
because even after launching, Dashboard effortlessly re-indexes user's resources
at every successful login.
