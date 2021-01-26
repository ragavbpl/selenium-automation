#include <Constants.au3>

;
; Author : Santosh Kumar Medchalam
;
;

Global $hWnd

Global $sGlobalFileName

logMessage("Starting the file upload for Chrome Browser")

If $CmdLine[0] = 0 Then

	  logMessage("No File was presented to upload, hence quiting")

	  exiting()

   Else

   $sGlobalFileName = $CmdLine[1]

   logMessage("The Presented file to upload is : "&$sGlobalFileName)

   logMessage("Waiting for initial 5 Seconds for the presence of the Open Dialogue Window"& @LF)

   WinWaitActive("Open","",5)

	   If WinExists("Open") Then

		 logMessage("The Presence of Open Dialogue Window is confirmed")

		 waitAndSetWindow(2)

		 setFileName($sGlobalFileName)

		 clickButton()

	   Else

		 logMessage("Waiting for 10 more Seconds for the presence of the Open Dialogue Window, as the initial wait failed")

		 waitAndSetWindow(10)

			If WinExists("Open") Then

			   logMessage("The Presence of Open Dialogue Window is confirmed, during the second wait")

			   waitAndSetWindow(2)

			   setFileName($sGlobalFileName)

			   clickButton()

			 Else

			  logMessage("The presence of the Open Dialogue Window failed, even after second wait, Hence Exiting")

			EndIf



	   EndIf

   exiting()

EndIf



	Func setFileName($sFileName)

	   logMessage("Move the Control to Edit1 Textfield")

	   ControlMove($hWnd, "", "Edit1", 0, 0)

	   Sleep(1000)

	   logMessage("Move the Focus to Edit1 Textfield")

	   ControlFocus("Open","","Edit1")

	   Sleep(1000)

	   logMessage("Setting the text in Edit1 Textfield to "&$sFileName)

	   ControlSetText("Open","","Edit1",$sFileName)

	   Sleep(3000)

   EndFunc


   	Func clickButton()

	   Sleep(1000)

	   logMessage("Move the Control to Button1 Button")

	   ControlMove($hWnd, "", "Button1", 0, 0)

	   Sleep(1000)

	   logMessage("Clicking on the Button1 Button")

	   ControlClick("Open","","Button1")

	   Sleep(5000)

   EndFunc

    Func waitAndSetWindow($waitTime)

	   $hWnd = WinWait("Open", "", $waitTime)

	   Sleep(1000)

	   logMessage("Window Handle : " & $hWnd)

	  If $hWnd = 0 Then

	    logMessage("Window Handle not found ")

	  Else

	   WinActivate($hWnd)

	   logMessage("Activated the Window : Open")

	  EndIf

    EndFunc

	Func logMessage($sMessage)

	ConsoleWrite("Chrome File Upload Worker : " & $sMessage & @LF)

    EndFunc

    Func exiting()

	logMessage("Exiting from Chrome File Uploader")

	Exit 0

    EndFunc



; Finished!
