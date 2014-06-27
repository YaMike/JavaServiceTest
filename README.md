JavaServiceTest
===============

Java service test (testService).

Service uses JNI to access native library. 
Library contains a POSIX thread which is 
executed by call from Java and invokes 
Java method via JNI each second.

Android Java Starter Application (testServiceStarter).
Used just to quickly start Java service.

Android Java nfctest application (nfctest).
nfctest application is a simple test demonstrating
how to work with nfc in android (mifare-ultralight).

Android notification test.
Shows notification with two buttons with callbacks.
