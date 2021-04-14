# Dashboard Logic

The life process of Dashboard includes important series of sets of calls
known as 'Sequences'.  These include **Verification**, **Launch**,
**Build**, and **Collapse Sequence**.

They are logical, and each has consequences on the succeeding sequence.
They involve intensive interaction of classes, several io-operations,
generation of components or even temporary files.

A sequence may undergo several distinct phases as needed, or where implemented.

## Verification Sequence
The Verification Sequence is the first and foremost of all the sequences.
Involves setting up the Dashboard, followed by authenticating the user.

Firstly, Dashboard strongly discourages **multiple instances**.
In a nutshell, if it's detected that Dashboard is already running, then any
subsequent launch attempt might potentially be ignored.

Furthermore, Dashboard relies on its **configuration** files to determine what
to do, at startup, and how to do. This includes **version checks**, and **user-matching**.

Where the configuration are missing, or otherwise not consistent, a new instance
will be triggered. This is what is known as a "First Run".
The student then will have to Login. But, if the configuration files are found
intact, Dashboard then will build from a serializable state.
Please see the [Dashboard](src/main/java/utg/Dashboard.java) class for more info.

## Launch Sequence
Following the **Verification Sequence**, is the **Launch Sequence**.
This is only triggered if the Verification succeeds.
This sequence will then start the job of generating the student's data -
which if it's a "First Launch", are given by [PrePortal](src/main/java/core/first/PrePortal.java),
otherwise are harvested from **Deserialization**.

The user's details are indispensable for the **Build Sequence**.
Therefore, if the de-serialization fails for whatever reason,
the Verification is asked to force the user back to the Login page,
undergoing a First Run. Failure of any other de-serializations -
like de-serialization of Tasks, Alerts, etc. - won't cause this retreat.

If it's a **First Run**, **serialization** obviously comes into play instead of **de-serialization**.
In this case, the Build Sequence does not wait on the Launch Sequence
as the user's data are readily available by [PrePortal](src/main/java/core/first/PrePortal.java).
The Launch Sequence, then, will complete its job by activating
[FirstLaunch](src/main/java/core/first/FirstLaunch.java) which performs
the serialization at dispose. See the [First](src/main/java/core/first) package.

## Build Sequence
Next is the **Build Sequence**. This involves loading the components for user-interaction.
Undergoes different phases and may not necessarily wait on the Launch to finish
but can never start before it.

The initial phase is the same for all the runs whether "First" or not.
If it's not "first", then a Remembrance is made eventually to reload
the previously saved settings & UI preferences.

See [Activity](src/main/java/core/Activity.java),
[Board](src/main/java/core/Board.java) and its collaborators.

## Collapse Sequence
**Collapse Sequence**, in a nutshell, involves preparing the data for the next
**Verification Sequence**. The Dashboard is made to hide prior to this serialization,
and the virtual machine is terminated afterwards. Finally, the root folder is,
entirely, overridden. Notice, Dashboard is independent of this directory within runtime.

The VM is terminated to relief the native system of any potential pending
charges. This can save memory and battery-life.

To forestall any unexpected shutdown, Dashboard also adds the
Collapse Sequence in the system's shutdown-hook.
This can minimize loss of data due to abnormal termination of the VM.

See [Serializer](src/main/java/core/serial/Serializer.java).
