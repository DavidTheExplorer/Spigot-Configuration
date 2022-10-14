# Spigot Configuration
This small library wraps Spigot's Configuration API in order to provide the following critical features it lacks:
* Elimination of parse logic from _onEnable()_.
* Convenient completion of missing sections whose default values are taken from the internal config(by the same path).
* Represent configs as OOP objects, including the _File_ object which is necessary for the _save()_ method.
