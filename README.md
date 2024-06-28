
# AppCoins SDK Unity package

## Description

Streamline the process of adding Appcoins SDK to your Unity app through importing from the Unity Package Manager. Below you can see the video about how to integrate the SDK and after it a detailed installation guide of the same process.

## Video

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/m-EZxdb7sUY/0.jpg)](https://www.youtube.com/watch?v=m-EZxdb7sUY)

## Installation Guide

### Step 1 - Import Package
* Start by opening on the top menu bar the Window > Package Manager
* In the new window on the top left corner click on the + sign and select Import via git URL and paste the following link: https://github.com/Aptoide/appcoins-sdk-unity-package
 * Wait to import and compile all files

### Step 2 - Move Folders * Open the Assets folder and the package folder 
* Move the folder and related metafiles of Scripts and Plugins folder to the Assets folder
 * Wait to compile files 


### Step 3 - Add AptoPurchaseManager to the Main Camera game object
* Open the Scripts folder and select the Main Camera game object * Drag and drop the AptoPurchaseManager to inspector side of the Main Camera

### Step 4 - Set the Params
* Set the params of KEY, Attempts and Developer Payload 

### Step 5 - Setup the Purchase Button 
* Select the Play button and in the inspector on the bottom add on the on click a new entry * Drag and drop the Main Camera to the box under Runtime (on OnClick section) 
* After that select the Script AptoPurchaseManager and the method StartPurchase 

### Step 6 - Setup the Consume Item Button
* Select the Consume button and in the inspector on the bottom add on the on click a new entry 
* Drag and drop the Main Camera to the box under Runtime (on OnClick section) 
* After that select the Script AptoPurchaseManager and the method ConsumeItem 


### Step 7 - Update the package name
* Open the Manifest file and update the package name to your project 
* Update as well on the OverrideExample the line 1 package name and the line 27 on the getSharedPreferences   


After that you can run and you have successfully integrate the Appcoins SDK on your Unity App through Package Manager.
