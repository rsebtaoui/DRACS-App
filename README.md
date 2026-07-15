# DRACS App

DRACS (Digital Resource for the "Direction Régionale d'Agriculture Casablanca settat") is an Android application designed to provide information and resources about this Administration (DRACS) in Morocco. The app serves as a digital guide for farmers and agricultural stakeholders, offering easy access to important information, forms, and procedures related to the services of this  administration.

## Features

- **RNA Section**: Information about the National Agricultural Registry
- **Content Management**: Firebase-powered content management system
- **Offline Support**: Cached content for offline access
- **Arabic Interface**: Full Arabic language support
- **Interactive Elements**: Clickable locations and downloadable forms

## Screenshots
![main](https://github.com/user-attachments/assets/b769bd15-a340-48d9-8beb-18626a2e243f)

![redirect](https://github.com/user-attachments/assets/9df66a43-09c1-48f7-9ea6-9102a140df50)

![drawer](https://github.com/user-attachments/assets/003858f7-e166-44ae-99d5-3ee871d9df97)

## Technical Requirements

- Android Studio Hedgehog | 2023.1.1
- Minimum SDK: 23 (Android 6.0)
- Target SDK: 35 (Android 15)
- Java 8

## Dependencies

```gradle
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
implementation("com.google.firebase:firebase-analytics")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-storage")

// UI Components
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.navigation:navigation-fragment:2.7.7")
implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

// Additional Libraries
implementation("com.github.ibrahimsn98:SmoothBottomBar:1.7.9")
implementation("com.github.cachapa:ExpandableLayout:2.9.2")
implementation("de.hdodenhof:circleimageview:3.1.0")
```

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/rsebtaoui/DRACS-App.git
   ```

2. **Firebase Setup**
   - Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
   - Add your Android app with package name `com.khalil.DRACS`
   - Download `google-services.json` and place it in the `app` directory
   - Enable Firestore Database
   - Create a collection named `pages` with documents: `rna`, `ps`, `fda`, `je`, `fp`

3. **Build Configuration**
   - Open the project in Android Studio
   - Sync Gradle files
   - Build the project

4. **Content Management**
   - Access the Firebase Console
   - Navigate to Firestore Database
   - Add content to each document under the `pages` collection using this structure:
     ```json
     {
       "sections": {
         "section_key_1": {
           "title": "عنوان القسم",
           "introduction": "المقدمة",
           "dashes": ["نقطة 1", "نقطة 2"],
           "conclusion": "الخاتمة",
           "order": 1,
           "clickable_words": [
             {
               "text": "تحميل النموذج",
               "color": "#0000FF",
               "action_type": "download",
               "action_value": "path_or_drive_url"
             }
           ],
           "colored_lines": [
             {
               "text": "سطر ملون",
               "color": "#FF0000"
             }
           ]
         }
       }
     }
     ```

     **Supported `action_type` values:** `download`, `map` (lat,lng), `web` (URL)

## Security Notes

- Never commit `google-services.json` to version control
- Use the provided `google-services.json.template` as a reference
- Keep your Firebase API keys and configuration secure
- Follow Firebase security rules best practices

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/khalil/DRACS/
│   │   │   ├── DRACSApplication.java
│   │   │   ├── Activities/
│   │   │   │   ├── SplashActivity.java
│   │   │   │   └── Activity_main.java
│   │   │   ├── Fragments/
│   │   │   │   ├── BaseContentFragment.java
│   │   │   │   ├── RNA.java, PS.java, FDA.java, JE.java, FP.java
│   │   │   │   ├── home.java, SearchFragment.java
│   │   │   │   └── settings.java, About.java
│   │   │   ├── Repository/
│   │   │   │   ├── ContentRepository.java
│   │   │   │   └── ContentCallback.java
│   │   │   ├── Adapters/
│   │   │   │   ├── ExpandableAdapter.java
│   │   │   │   ├── SearchAdapter.java
│   │   │   │   └── ShimmerAdapter.java
│   │   │   ├── Models/
│   │   │   │   └── FirestoreModel.java
│   │   │   └── Utils/
│   │   │       ├── DataPreFetcher.java
│   │   │       ├── ConnectionUtils.java
│   │   │       └── FileUtils.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── fragment_content_page.xml
│   │   │   │   └── fragment_settings.xml
│   │   │   └── navigation/
│   │   │       └── nav_main.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── google-services.json (DO NOT COMMIT THIS FILE)
```

## Firebase Integration

The app uses Firebase for:
- Content Management (Firestore)
- Analytics
- Crashlytics

### Firestore Structure

```
pages (Collection)
├── rna (Document)   → السجل الوطني الفلاحي
├── ps  (Document)   → الحماية الاجتماعية
├── fda (Document)   → صندوق التنمية الفلاحية
├── je  (Document)   → المقاولين الشباب
└── fp  (Document)   → التكوين المهني

Each document contains:
  sections: Map<String, Section>
    Section:
      ├── title: String
      ├── introduction: String
      ├── dashes: String[]
      ├── conclusion: String
      ├── order: Number
      ├── clickable_words: Array<{ text, color, action_type, action_value }>
      └── colored_lines: Array<{ text, color }>
```

## Features in Detail

### RNA Section
- Expandable content sections
- Interactive maps for locations
- PDF form downloads
- Offline content access

## License

MIT License

Copyright (c) 2024 DRACS App


## Contact

For support or inquiries, please contact us
