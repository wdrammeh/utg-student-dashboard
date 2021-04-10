# Dashboard Logic

The life process of Dashboard includes important series of sets of calls
known as 'Sequences'.
These are **Verification**, **Launch**, **Build**, and **Collapse Sequence**.

They are logical, and each has consequences on the succeeding sequence.
They involve an intensive interaction of classes, several io-operations,
generation of components or even temporary files.

## Verification Sequence
The Verification Sequence is the first and foremost of all the sequences.
Verification does not only mean verifying the user through the Portal on a "first run".
In fact, at every startup, Dashboard gets into this sequence of comparing the previous
userName with the present. If the two match, then the details will be generated,
and the Launch Sequence is triggered forthwith. Please see utg.Dashboard for more info.

## Launch Sequence
Following the Verification Sequence, is the Launch Sequence.
This is only triggered if the Verification succeeds.
The Verification is said to be successful, if either the user is just been through
the Portal, or the current user's name matches the previous userName with the
core-details of the student found to be intact.

This sequence will then start the job of generating the user's details -
which if it's a "first launch", are given by main.PrePortal,
otherwise are harvested from de-serialization.
The user's details are indispensable for the Build Sequence.
Therefore, if the de-serialization fails for whatever reason,
the Verification is asked to force the user back to the Login page.
Failure of any other de-serializations - like de-serialization of Tasks, Alerts, etc. -
won't cause this retreat.

If it's a "first run", serialization obviously comes into play instead of de-serialization.
In this case, the Build Sequence does not wait on the Launch Sequence
as the user's data are readily available by main.PrePortal. The Launch Sequence, then,
will complete its job by activating the main.FirstLaunch type which performs
the serialization at dispose. See main.FirstLaunch.

## Build Sequence
Next is the Build Sequence. This involves loading the components for user-interaction.
This undergoes different phases and may not necessarily wait on the Launch to finish
but can never start before it.
The initial phase is the same for all the runs whether "first" or not.
If it's not "first", then a Remembrance is made eventually to reload
the previously saved settings & UI preferences.
See main.Board, and its collaborators.

## Collapse Sequence
Collapse Sequence, in brief, involves preparing the data for the next Verification Sequence.
The Dashboard is made to hide prior to this serialization,
and the virtual machine is terminated afterwards. Finally, the root folder is,
entirely, overridden. Notice, Dashboard is independent of this directory within runtime.
The VM is terminated to relief the native system of any potential pending
charges. This saves memory and battery-life. To forestall any unexpected shutdown,
Dashboard also adds the Collapse Sequence in the system's shutdown-hook.
This can minimize loss of data due to abnormal termination of the VM.
See main.Board, main.Serializer.
