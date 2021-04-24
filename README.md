# UTG-Student Dashboard

`v0.1.1`

A flexible and elegant student management system for the University of The Gambia.

Dashboard is built by the students for the students.

## ERP System
Dashboard uses the **UTG ERP System (Portal)** as a center of verification for users.
The Portal is Dashboard's sole source of valid data. The data collected are used for,
but not limited to, **verification**, **analysis**, and **presentation**.
Read [Dashboard's Privacy Policy](PRIVACY.md) for more information.

At every successful login, Dashboard grasps the fundamental details of the user.
So students won't need to be specifying level, status on the go.
 
It is however important to note that the analysis provided by Dashboard
is entirely **Portal-independent**.
Therefore, unexpected details from therein can induce misbehavior of Dashboard.
We advise every student victim of wrong, or incomplete details on their Portals
to refer to their respective departments for help before, or anytime soon after,
launching Dashboard.

We however handle, gracefully, the common issue of missing-grades,
but cannot afford to lose core details like the student's name,
or matriculation number. Dashboard may halt build, if such details
are missing, or not readable somehow.
Besides, some students have conflicting information in their Portals.
For instance, a student admitted in 2016 may have his/her
Year of Admission as 2019 on the portal.
To mention a few consequences of this is that, obviously,
Dashboard will no way accurately predict the Expected Year of Graduation,
or the Current Level of the student.
Plus, mis-indexing of modules' years will occur which, in turn,
may cause analysis-by-year problems, and addition of modules to the inappropriate tables.

The good news is that, all these, if occurred, can be fixed at any point in time,
because even after launching, Dashboard effortlessly re-indexes user's resources
at every successful login.

## What Dashboard can do
- Offline content management capability.
- Unlimited **Transcript Exportation** in Portable Document Format (PDF).
- News updates from UTG official news site (download once and for all).
- Explore UTG Portal.
- Track semesters on the go.
- Course Collection, Classification, Organization, Analysis & Presentation.
- Semester to semester performance graph.
- TODO, Tasks, and Projects management.
- Assignment management system with notification triggers.
- UTG FAQs and Answers.

## What Dashboard cannot do
- Cannot be used as a mean of application for non-enrolled students.
- Cannot apply deferments for enrolled students.

## System Requirements
Presently, Dashboard depends on the traditional **Firefox Web Browser**
for the "scrapping" functionality.
Therefore, **Firefox** is a requirement for Dashboard to effectively
communicate with the Portal.

## Try Dashboard
Dashboard has a **Trial** Functionality by the means of which non-UTG students
can get to temporarily use it, exploring the vast features therein.
Should a student wish, he/she can try out the **Trial** as well,
but maximum functionality is only provided when user is logged in as a **UTG Student**.

## "A journey of thousand miles begins with a single step"
Dashboard is an ambitious project, and this is only the beginning.
Also, we wish to integrate the following features in a future release:

- Course Registration Functionality
- Student Voting System
- Student Forum
- Student Auditing System

## License
(c) 2021 UTG. All rights reserved. Read the [LICENSE](LICENSE.txt) file.

## Privacy & Terms
Whatever happens in your Dashboard, stays in your Dashboard.
Read the [PRIVACY](PRIVACY.md) file.

## Maintainers
- [Muhammed W. Drammeh <md21712494@utg.edu.gm>](https://www.github.com/w-drammeh)
