Shruti Susarla, Nadia Aimandia, Tiffany Tren

To install and run the software
* Download Andriod Studio Android Studio Ladybug Feature Drop | 2024.2.2 Patch 2
* Add the following to build.gradle in app folder:
* Andriod compileSdk 33
* dependcies used for build.gradle file
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Retrofit for network calls
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    // Gson for JSON parsing
    implementation 'com.google.code.gson:gson:2.9.0'

    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'

}
*  all php files are in the Phase 3 folder. Copy the folder into htdocs XAMPP
*  Change url in string.xml to contain your IP ADDRESS. string.xml is found in app/main/res/values
