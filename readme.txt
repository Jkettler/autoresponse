App Name: Auto-Response

----------------------------------------------------------------
Team:
John Lynch     JRL2755
Jimmy Kettler  JBK97
Kevin Kinney   KPK297

----------------------------------------------------------------
Description of app:
The basic principle of this app is that you can create 'events'
consisting of some set of conditions and a responses, so that
if the conditions are met, the response is carried out. For 
example, the conditions could be that it is between 2pm and 3pm
on a Monday, Wednesday, or Friday, and your location corresponds
to UT. Your response might be to put your phone on silent and
automatically respond to any incoming texts with a default 
message.

----------------------------------------------------------------
Instructions for use:
From the opening screen, you can create new events, or go to a
list of already existing events and edit them. When creating an
event, you will walk through a few screens setting the conditions
and the response. Once an event is created the app will monitor
for the specified conditions and, if they are met, will respond
as specified.

----------------------------------------------------------------
Features completed:
Features are catagorized as either as conditions/responses for 
events or miscellaneous

From prototype:
 Completed:
   Conditions:
    - day
    - time
    - location
    - incoming text
   Responses:
    - change phone mode
    - set reminders
    - automate text responses
   Miscellaneous
    - ability to add and save locations
    - ability to combine conditions
 Uncompleted:
   Conditions:
    - driving
   Miscellaneous
    - delay in auto responding to texts, allowing user to 
      override.
Not from prototype:
 Miscelleneous:
    - ability to view/edit pre-existing events

----------------------------------------------------------------
References:

Code for the name entry dialog box was based off of this: http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
The code for changing phone modes was based off of this: http://www.androidsnippets.com/change-phone-mode-to-silent-vibrate-normal  
This example was used as a basis for making the AutoResponseEvent that is passed around by the activities via the Parcelable interface: http://shri.blog.kraya.co.uk/2010/04/26/android-parcel-data-to-pass-between-activities-using-parcelable-classes/
Our code for using SharedPreferences, location, and use of services was based off of the slides posted on the schedule as well as the tutorials.

----------------------------------------------------------------
Classes written:

 ConditionSelectorActivity
  - An activity for selecting conditions for an event
 EventListActivity
  - An activity for listing events to be viewed and edited
 HomeScreenActivity
  - A home screen for the app
 LocationCreatorActivity
  - An activity used save the phone's current location.
 LocationSelectorActivity
  - An activity to select locations from a list to be used as 
    conditions.
 ResponseSelectorActivity
   - An activity to select responses for a set of conditions.
 AutoResponseEvent
  - A data structure to store event/response info in.
 MyService
  - A background service to monitor conditions and execute 
    responses.
 NotificationMessage
  - An empty class that will later be used to define a response 
    when status bar notifications are clicked.
 PreferenceHandler
  - A set of functions used to easily write/read saved preferences
