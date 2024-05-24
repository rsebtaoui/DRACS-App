package com.example.DRACS;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RNA extends Fragment {

    private static final String CHANNEL_ID = "download_channel";
    private static final int NOTIFICATION_ID = 1;
    private RecyclerView recyclerView;
    private ExpandableAdapter adapter;


    public RNA() {
        // Required empty public constructor
    }

    public static RNA newInstance(String param1, String param2) {
        RNA fragment = new RNA();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_r_n_a, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //this is the list of the clickable words that we passe as a parameter
        List<Item.ClickableWord> clickableWordsR_centers = new ArrayList<>();

        //handling the click of the word "الجديدة"
        clickableWordsR_centers.add(new Item.ClickableWord("الجديدة", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.247973, -8.502161,"(المديريات+الإقليمية+للفلاحة+بالجديدة)");
            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("الدار البيضاء ", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.594278, -7.601056,"(المديريات+الإقليمية+للفلاحة+بالبيضاء)");
            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("بنسليمان", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.6090340,-7.1259160,"(المديريات+الإقليمية+للفلاحة+ببنسليمان)");            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("سطات", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.0100280, -7.6162690,"(المديريات+الإقليمية+للفلاحة+بسطات)");            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("برشيد", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(33.264458, -7.581894,"(المديريات+الإقليمية+للفلاحة+ببرشيد)");            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("سيدي بنور", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(32.65750840219919, -8.425363994368995,"(المقاطعة+التابعة+للمكتب+الجهوي+للاستثمار+الفلاحي+لدكالة+ببسيدي+بنور)");          }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("خميس الزمامرة", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps(32.6305907107246, -8.754465575491885,"(المقاطعة+التابعة+للمكتب+الجهوي+للاستثمار+الفلاحي+لدكالة+بخميس+الزمامرة)");            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("أولاد فرج", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this function OpenGoogleMaps take 3 arguments (latitude,longitude,title you wanna show in the location)
                openGoogleMaps(32.95870479583255, -8.221324423614597,"(المقاطعة+التابعة+للمكتب+الجهوي+للاستثمار+الفلاحي+لدكالة+بأولاد+فرج)");           }
        }));

        clickableWordsR_centers.add(new Item.ClickableWord("تحميل إستمارة طلب التسجيل", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFileFromAssets(v.getContext(), "إستمارة طلب التسجيل.pdf");            }
        }));

        clickableWordsR_centers.add(new Item.ClickableWord("تتمة الطلب", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFileFromAssets(v.getContext(), "تتمة إستمارة طلب التسجيل .pdf");
            }
        }));

        clickableWordsR_centers.add(new Item.ClickableWord("تحميل الطريقة", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //the link to open video in youtube
            }
        }));


        clickableWordsR_centers.add(new Item.ClickableWord("تحميل استمارة طلب تحيين", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFileFromAssets(v.getContext(), "استمارة طلب تحيين.pdf");
            }
        }));
        clickableWordsR_centers.add(new Item.ClickableWord("تحميل استمارة طلب التشطيب", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFileFromAssets(v.getContext(), "استمارة طلب التشطيب.pdf");
            }
        }));


        //setting the content fo the expandable places
        List<Item> items = new ArrayList<>();

        //item parameters(title,intro,content,conclu,list_of_clickable_words)
        //the item have title that will be clickable and inside the expanding space there is:
        // introduction and content and conclusion .
        // along with an other parameter witch is the list of clickable words
        items.add(new Item("  ▼  الامتيازات:", "","- الاستفادة من الإعانات المقدمة من طرف وزارة الفلاحة \n" +
                "- الاعتراف القانوني بالاستغلالية الفلاحية وإمكانية تتبعها: مما يُسهل عليهم الوصول إلى مختلف الخدمات والفرص المتاحة\n" +
                "- تسهيل الوصول إلى المساعدات والبرامج المقدمة من وزارة الفلاحة: برامج التدريب والدعم والاستشارة التقنية الفلاحية\n" +
                "- تسهيل الولوج إلى التمويل والحماية الاجتماعية لتحسن الظروف المعيشية للفلاحين\n" +
                "- تعزيز العلاقة بين الدولة والمستغلين الفلاحيين: يمكن الوزارة من تلبية أفضل لاحتياجات الفلاحين واستهداف الإعانات","", clickableWordsR_centers));

        items.add(new Item("  ▼  الوثائق المطلوبة للتسجيل:", "تجدر الإشارة الى ان التسجيل في السجل الوطني الفلاحي يخص المستغلين الذاتيين والمستغلين الاعتبارين أو التنظيمات مثل التعاونيات الفلاحية والشركات الفلاحية،",
                "- تعبئة استمارة طلب التسجيل الاستغلالية الفلاحية في السجل الوطني الفلاحية (معلومات عن الاستغلالية الفلاحية والنشاط الفلاحي والوسائل المسخرة) <<تحميل إستمارة طلب التسجيل >> و << تتمة الطلب >> \n" +
                        "- نسخة من البطاقة الوطنية للتعريف الإلكترونية للمستغل الذاتي\n" +
                        "----> بالنسبة للأجانب: نسخة من سند الإقامة؛\n" +
                        "- بالنسبة للتنظيمات: نسخة من البطاقة الوطنية للتعريف الإلكترونية للممثل القانوني للشخص الاعتباري عند الاقتضاء؛\n" +
                        "- تسمية الشخص الاعتباري وطبيعته ومقره الاجتماعي؛\n" +
                        "- وثيقة تثبت الطبيعة القانونية للعقار موضوع الاستغلالية؛\n" +
                        "- الإحداثيات الجغرافية للبقع الفلاحية للضيعة الفلاحية ومساحتها.<<تحميل الطريقة>>",
                "يتسلم المستغل \"وثيقة المعلومات الخاصة باستغلاليته\" وتتوفر الإدارة على اجل أقصاه 30 يومًا لفحص الملف والتحقق منه إذا كانت المعلومات متوافقة، يتلقى المستغل شهادة التسجيل في السجل الوطني الفلاحي. إذا كانت المعلومات غير متوافقة أو غير دقيقة، فلدى المستغل 45 يومًا لتصحيح طلبه وإعادة تقديمه",
                clickableWordsR_centers));

        items.add(new Item("  ▼  مراكز التسجيل:","يمكن تقديم طلبات التسجيل والتحيين والتشطيب على مستوى:\n" ,
                "- المديريات الإقليمية  للفلاحة (DPA): \n" +
                        "الجديدة، الدار البيضاء ، بنسليمان، سطات، برشيد\n" +
                        "- والمقاطعات التابعة للمكتب الجهوي للاستثمار الفلاحي بدكالة  (ORMVAD):\n" +
                        " : سيدي بنور، خميس الزمامرة، أولاد فرج، الغربية",
                "", clickableWordsR_centers));



        items.add(new Item("  ▼  تحيين المعلومات:", "يسمح للمشغل القيام بتعديل المعلومات مع تقديم جميع الوثائق التي تثبت صحة المعلومات المدلى بها في غضون 3 أشهر.",
                "- تعبئة استمارة طلب تحيين المعطيات التي سبق التصريح بها <<تحميل استمارة طلب تحيين>>",
                "", clickableWordsR_centers));

        items.add(new Item("  ▼  التشطيب من السجل:", "يأتي تشطيب الاستغلالية الفلاحية بعد توقف النشاط الفلاحي، بناء على طلب المستغل الفلاحي",
                "- تعبئة استمارة طلب التشطيب على تقييد الاستغلالية الفلاحية <<تحميل استمارة طلب التشطيب>>",
                "", clickableWordsR_centers));

        items.add(new Item("  ▼  أسئلة وأجوبة:", "","-- هل التسجيل في السجل الوطني الفلاحي إلزامي للاستفادة من إعانات وزارة الفلاحة؟\n" +
                "\n" +
                "نعم، للاستفادة من الإعانات المقدمة من طرف وزارة الفلاحة، يجب عى المستغل الفلاحي تسجيل إستغلاليته في السجل الوطني الفلاحي\n" +
                "-- هل يستطيع وكيل المستغل الفلاحي التسجيل بدلاً من المستغل الفلاحي نفسه؟\n" +
                "\n" +
                "تسجل كل استغلالية فلاحية في السجل الوطني الفلاحي من طرف المستغل الفلاحي أو وكيله المعتمد بناء على طلب مقدم إلى الإدارة.\n" +
                "-- ما هي العقوبات القانونية في حالة الإدلاء ببيانات كاذبة؟\n" +
                "\n" +
                "يعاقب بغرامة من 5000 إلى 20000 درهم كل مستغل أدلى بسوء نية بتصريح كاذب يخص المعطيات المتعلقة بالاستغلالية\n" +
                "الفلاحية في السجل الوطي الفلاحي. وترفع الغرامة إلى عرة ) 10 ( أضعاف إذا كان المستغل شخصا اعتباريا.\n" +
                "-- هل يؤثر التسجيل في السجل الوطني الفلاحي على ملكية الأراضي الفلاحية؟\n" +
                "\n" +
                "لا يؤثر التسجيل في السجل الوطني الفلاحي على ملكية الأراضي الفلاحية.\n" +
                "-- هل هناك علاقة بين السجل الوطني الفلاحي والضرائب؟\n" +
                "\n" +
                "السجل الوطي الفلاحي هو آلية سيتم استخدامها حصريًا في التنمية الفلاحية ولا ترتبط بالضريبة الفلاحية كما تقع مسؤولية\n" +
                "الضرائب الفلاحية عى عاتق المديرية العامة للضرائب وليس عى عاتق وزارة الفلاحة. كما يحظر قانون حماية الأشخاص الذاتين\n" +
                "اتجاه معالجة المعطيات ذات الطابع الشخصي استخدام هذه البيانات خارج إطار التنمية الفلاحية.","", clickableWordsR_centers));

        items.add(new Item("  ▼  الإطار القانوني المنظم:", "","- الظهير رقم 36-22-1 بتاريخ 24 ماي 2022 تنفيذا للقانون رقم 80.21 بإحداث السجل الوطني الفلاحي\n" +
                "- المرسوم رقم 2-22-472 بتاريخ 3 غشت 2022 المتعلق بتطبيق قانون السجل الوطني الفلاحي\n" +
                "والقرار الوزاري رقم 22-2139 بتاريخ 23 مايو 2023 الذي يحدد نماذج طلبات تقييد \n" +
                "- الاستغلاليات الفلاحية في السجل الوطني الفلاحي، وتحين المعطيات، والتشطيب على التقييد، وكذا نموذج شهادة التقييد.","", clickableWordsR_centers));


        //set the adapter to show the content
        adapter = new ExpandableAdapter(getContext(), items);
        recyclerView.setAdapter(adapter);

        return view;
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Download Channel";
            String description = "Channel for download notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void showDownloadNotification(Context context, String filePath) {
            // Create an intent to open the file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(filePath);
            Uri uri;

            //Avoiding API level bugs/Exceptions using this test
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // For API 24 and above, use FileProvider
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                // For lower API levels, use Uri.fromFile()
                uri = Uri.fromFile(file);
            }

            intent.setDataAndType(uri, "application/pdf");

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle("Download Complete")
                    .setContentText("File downloaded to: " + filePath)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)  // Set the pending intent
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }



        private void copyFileFromAssets(Context context, String fileName) {
        createNotificationChannel(context);

        // Copy file from assets to internal storage even if it is already exist suing the unique name
        File internalFile = new File(context.getFilesDir(), fileName);
        if (!internalFile.exists()) {
            try (InputStream is = context.getAssets().open(fileName);
                 FileOutputStream fos = new FileOutputStream(internalFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to download file to internal storage.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create a unique file name for the copy in external storage
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        File externalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), uniqueFileName);

        try (InputStream is = new FileInputStream(internalFile);
             OutputStream os = new FileOutputStream(externalFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            Log.i("Download", "File downloaded to: " + externalFile.getAbsolutePath());
            Toast.makeText(context, "File downloaded to: " + externalFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Show notification
            showDownloadNotification(context, externalFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to download file to external storage.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openGoogleMaps(double latitude, double longitude,String label) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latitude + "," + longitude + label);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
