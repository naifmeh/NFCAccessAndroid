# NFCAccessAndroid

ANDROID APP EXPLANATIONS
********************************
Android application used to manage the NFC access system. 
Divided in 5 parts : Sign in - Profile - Database - Emulation - Sign up

Using the VOLLEY Framework as recommanded in the Android documentation, to send HTTP REQUEST. Sign in will use a GET request, Database as well along with a DELETE request. Sign up will use a POST request with a JSON body.

Emulation use the AID F0010203040506 which is triggered with the SELECT AID APDU command :

		00A4040007F0010203040506

HCE will respond with UID+0x9000 if emulation activated or 0x0000 else.

/!\ ISSUE IN THE CODE : SOMETIMES ANDROID SEND AN INVALID COMMAND WHICH MAKE THE DRIVERS FAIL IN THE READER. ISSUE IS PARTIALLY FIXED WITH SCRIPT RUNNING ON RASPBERRY (SEE NFCAccessScriptDock REPO FOR MORE INFOS) /!\


USER OF RANK 3 CAN DELETE EVERYONE. USERS OF RANK 2 CAN DELETE USERS OF LOWER RANK. USERS OF RANK 1 CAN ONLY ACCESS THE DATABASE. USERS OF RANK 0 CAN ONLY EMULATE AND ENTER THE DOOR.


USE ONLY ON A NFC COMPATIBLE DEVICE.


