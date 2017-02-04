#Sending a photo using the Camera

Atlas provides a simple way for you to take an image via the camera and send it as a `ThreePartImage`, via the `CameraSender` class. 

`CameraSender` requires that you implement a `FileProvider` in your application, in case you have not already. To create a `FileProvider`, add a `<provider>` tag similar to the following in your `AndroidManifest.xml`:

```
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.file_provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
</provider>
```
Create a `provider_paths.xml` file in the `res/xml` folder. The folder may need to be created if it does not exist. Add the following:
```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="."/>
</paths>
```

Then supply the authority to the `CameraSender` when creating an instance.

```
new CameraSender(title, iconResourceId, mActivity, {authority})
```

where `{authority}` in the above instance can be provided as `mActivity.getApplicationContext().getPackageName() + ".file_provider"`

or if you have already implemented a `FileProvider`, just supply the authority to the `CameraSender`