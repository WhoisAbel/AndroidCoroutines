# AndroidCoroutines


In this priject I take you through your first kotlin coroutines example.

If you are complete beginner when it comes to coroutines and want to see what they're all about, this is the project for you.

The main goal of coroutines is to "simplify asynchronous work by getting rid of callbacks." Coroutines are NOT threads. I like to think of coroutines as JOBS. And each "job" may contain "child jobs". Jobs that can run in any thread. Many coroutines can run in a single thread at once.

They're kind of "like" threads because you can start them up and do work asynchronously, but they aren't threads. Many coroutines can exist and be running in a single thread. 

The main dispatchers for building coroutines is:
1) Default (CPU intensive work)
2) Main (UI Interactions)
3) IO (Input/output. ex: network or disk transactions)
