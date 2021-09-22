# Glossary

**Build**: Defines all the operations during which the Dashboard is triggered from a dormant state to an active state. This starts from the moment the Dashboard is called up till shortly after the UI is made visible.

**Collapse**: Defines the sequence of operations during which the Dashboard is made to terminate all processes and disposes off the UI. After this state, Dashboard is literally "dead" - no background tak runs - untill it is being rebuilt.

**Self silent**: Used to describe methods that contained and suppress
any potential exceptions within their blocks. Such methods, depending on the return type, may return `null` object or a `negative` number.
