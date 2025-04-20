package com.khalil.DRACS.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khalil.DRACS.Adapters.ExpandableAdapter;
import com.khalil.DRACS.Avtivities.Activity_main;
import com.khalil.DRACS.Utils.FileUtils;
import com.khalil.DRACS.Models.Item;
import com.khalil.DRACS.R;

import java.util.ArrayList;
import java.util.List;

public class PS extends Fragment {

    private RecyclerView recyclerView;
    private ExpandableAdapter adapter;

    public PS() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_p_s, container, false);

        recyclerView = view.findViewById(R.id.psrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //this is the list of the Colored lines that we passe as a parameter
        List<Item.Coloredlines> coloredlinesps = new ArrayList<>();

        //this is the list of the clickable words that we passe as a parameter
        List<Item.ClickableWord> clickableWordsps = new ArrayList<>();

        //handling clickable words
        clickableWordsps.add(new Item.ClickableWord("الجديدة", v -> FileUtils.openGoogleMaps(v.getContext(), 33.247973, -8.502161, "المديرية+الإقليمية+للفلاحة+بالجديدة")));
        clickableWordsps.add(new Item.ClickableWord("الدارالبيضاء", v -> FileUtils.openGoogleMaps(v.getContext(), 33.594278, -7.601056, "المديرية+الإقليمية+للفلاحة+بالبيضاء")));
        clickableWordsps.add(new Item.ClickableWord("بنسليمان", v -> FileUtils.openGoogleMaps(v.getContext(), 33.6090340, -7.1259160, "المديرية+الإقليمية+للفلاحة+ببنسليمان")));
        clickableWordsps.add(new Item.ClickableWord("سطات", v -> FileUtils.openGoogleMaps(v.getContext(), 33.0100280, -7.6162690, "المديرية+الإقليمية+للفلاحة+بسطات")));
        clickableWordsps.add(new Item.ClickableWord("برشيد", v -> FileUtils.openGoogleMaps(v.getContext(), 33.264458, -7.581894, "المديرية+الإقليمية+للفلاحة+ببرشيد")));
        clickableWordsps.add(new Item.ClickableWord("سيدي بنور", v -> FileUtils.openGoogleMaps(v.getContext(), 32.65750840219919, -8.425363994368995, "المقاطعة+التابعة+للمكتب+الجهوي+للاستثمار+الفلاحي+لدكالة+ببسيدي+بنور")));
        clickableWordsps.add(new Item.ClickableWord("خميس الزمامرة", v -> FileUtils.openGoogleMaps(v.getContext(), 32.6305907107246, -8.754465575491885, "المقاطعة+التابعة+للمكتب+الجهوي+للاستثمار+الفلاحي+لدكالة+بخميس+الزمامرة")));
        clickableWordsps.add(new Item.ClickableWord("أولاد فرج", v -> {
            //this function OpenGoogleMaps take 3 arguments (latitude,longitude,title you wanna show in the location)
            FileUtils.openGoogleMaps(v.getContext(), 32.95870479583255, -8.221324423614597, "المقاطعة+التابعة+للمكتب+الجهوي+للاستثمار+الفلاحي+لدكالة+بأولاد+فرج");
        }));
        clickableWordsps.add(new Item.ClickableWord("استمارة طلب التسجيل", v -> FileUtils.copyFileFromAssets(v.getContext(), "إستمارة الفلاح الخاصة بطلب التسجيل في التغطية الصحية للفلاحين.pdf")));
        clickableWordsps.add(new Item.ClickableWord("استمارة طلب تحيين المعطيات", v -> FileUtils.copyFileFromAssets(v.getContext(), "إستمارة الفلاح الخاصة بطلب تحيين المعلومات الخاصة بالتغطية الصحية.pdf")));
        clickableWordsps.add(new Item.ClickableWord("استمارة طلب التشطيب", v -> FileUtils.copyFileFromAssets(v.getContext(), "تصريح بالشرف بعدم ممارسة أي نشاط فلاحي.pdf")));

        coloredlinesps.add(new Item.Coloredlines("كيف تتم معالجة الطلبات؟"));

        coloredlinesps.add(new Item.Coloredlines("معرفة مال الطلبات الموضوعة بمراكز التسجيل؟"));


        //setting the content fo the expandable places
        List<Item> items = new ArrayList<>();

        items.add(new Item("  ▼  مقدمة:",
                "في 29 يوليوز 2020 ، خلال الخطاب الملكي بمناسبة عيد العرش المجيد، أعطى جلالة الملك نصره لله، تعليماته السامية \n" +
                        "للنهوض بالقطاع الاجتماعي و تحسيت ظروف المواطنين",
                "- تعميم التأمين الإجباري عن المرض لجميع الفئات النشيطة وغير النشيطة(بين سنتي 2021 و 2022) \n" +
                        "- تعميم التعويضات العائلية خلال سنتي 2023 و 2024 ، للأسر المعوزة التي لا تستفيد منها، مما يسمح لهم بتلقي إعانات إضافية تعينهم على تغطية مصاريف أطفالهم. \n" +
                        "- توسيع قاعدة المنخرطين بأنظمة التقاعد في أفق سنة 2025 ، لتشمل ايضا العمال غير الأجراء وغير الحاصلين على أي معاش تقاعدي. \n" +
                        "- تعميم التعويضات عن فقدان العمل في أفق سنة 2025 لتشمل جميع الأشخاص الذين لديهم عمل مستقر، وتوسيع قاعدة المستفيدين. \n",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  الامتيازات:",
                "على المستوى العملي، ستتيح التغطية الصحية الإجبارية للفلاحين، وخاصة صغارهم، ولأسرهم (الأزواج والأبناء) , بالنسبة للأطفال حتى سن 21 عاما و 26 عاما إذا كانوا متمدرسين, أو مدى الحياة بالنسبة للمعاقين. \n" +
                        "الاستفادة من رعاية صحية مماثلة لتلك التي يقدمها نظام التأمين الصحي للأجراء الذي يديره الصندوق الوطني للضمان الاجتماعي. في هذا السياق، تحدد المادة 7 من القانون 65.00 المزايا التي سيحصل عليها الفلاح المسجل في نظام التأمين الصحي الإجباري. وتشمل هذه المزايا:",
                "- الرعاية الطبية، بما في ذلك الجراحة، في المستشفيات.\n" +
                        "- الرعاية المتعلقة بالمتابعة من الحمل إلى الولادة.\n" +
                        "- علاج الأسنان والبصريات. ",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  الوثائق المطلوبة للتسجيل:",
                "تجدر الإشارة الى ان تسجيل الفلاحين في التغطية الاجتماعية يتم حسب فئة الدخل في لوائح العمال غير الأجراء.",
                "تعبئة استمارة طلب التسجيل مصادق عليها\n" +
                        "نسخة من البطاقة الوطنية للتعريف الإلكترونية مصادق عليها  \n" +
                        "وثيقة تثبت الطبيعة القانونية للعقار موضوع الاستغلالية.",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  تحيين المعلومات:",
                "يسمح للفلاحين القيام بتعديل المعلومات ",
                "تعبئة استمارة طلب تحيين المعطيات مصادق عليها \n" +
                        "نسخة من البطاقة الوطنية للتعريف الإلكترونية مصادق عليها  \n" +
                        "ما يثبت النقصان أو الزيادة على مستوى المساحة المستغلة أو أعدد الماشية",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  التشطيب:",
                "يأتي تشطيب بعد توقف النشاط الفلاحي، بناء على طلب ",
                "تعبئة استمارة طلب التشطيب مصادق عليها \n" +
                        "نسخة من البطاقة الوطنية للتعريف الإلكترونية مصادق عليها  \n" +
                        "ما يثبت نهاية ممارسة مهنة الفلاحة",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  مراكز التسجيل:",
                "يمكن تقديم طلبات التسجيل والتحيين والتشطيب على مستوى:",
                "المديريات الإقليمية للفلاحة ( (DPA: الجديدة، الدارالبيضاء، بنسليمان، سطات، برشيد)\n" +
                        "والمقاطعات التابعة للمكتب الجهوي للاستثمار الفلاحي لدكالة (ORMVAD) : سيدي بنور، خميس الزمامرة، أولاد فرج، الغربية",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  أسئلة وأجوبة:  ",
                "",
                "كيف تتم معالجة الطلبات؟\n" +
                        "\n" +
                        "يتم وضع الطلبات في مراكز التسجيل الموجودة بمختلف المديريات الإقليمية للفلاحة والمقاطعات التابعة للمكتب الجهوي للاستثمار الفلاحي لدكالة، هده الطلبات يتم إرسالها إلى مديرية الإستراتيجيات والإحصائيات بالرباط من أجل فحصها قبل إرسالها إلى الصندوق الوطني للضمان الاجتماعي من أجل البث فيها.\n" +
                        "حيث إن وزارة الفلاحة تتولى، بوصفها هيئة التنسيق، مسؤولية مهمتين رئيسيتين ألا وهما: تحديد وتحيين لوائح الفلاحين المستفيدين\n" +
                        "وإحالتها إلى الصندوق الوطني للضمان الاجتماعي لتسجيلها، وكذا التشطيب في حالة التوقف عن ممارسة أي نشاط فلاحي\n" +
                        "\n" +
                        "معرفة مال الطلبات الموضوعة بمراكز التسجيل؟\n" +
                        "\n" +
                        "لمعرفة الجواب عن طلبكم المقدم من طرف الفلاحين، يجب الاتصال بوكالات الصندوق الوطني للضمان الاجتماعي او الاتصال بمراكز النداء عبر الرقم الهاتفي: 2020 19 0520",
                "", clickableWordsps, coloredlinesps));

        items.add(new Item("  ▼  الإطار القانوني الحماية الاجتماعية:",
                "حرصاً على تفعيل التزام الدولة بسياسة تعميم الحماية الاجتماعية، وضعت الحكومة إطاراً تنظيمياً جديداً للتغطية الصحية ونظام معاشات التقاعد، يتألف من النصوص التشريعية التالية:",
                "القانون رقم 65.00 الصادر في 3 أكتوبر 2002 المتعلق بقانون التغطية الصحية الأساسية.\n" +
                        "بناءً على أحكام المادتين ( 2) و( 4) من هذا القانون، تم اعتماد قانونين آخرين لتنظيم التغطية الصحية للعمال المستقلين.\n" +
                        "القانون رقم 98 - 15 : يتعلق بنظام التأمين الإجباري الأساسي عن المرض الخاص بفئات المهنيين والعمال المستقلين والأشخاص غير الأجراء الذين يزاولون نشاطا خاصا. صدر هذا القانون بموجب ظهير 1- 17 - 15 بتاريخ 23 يونيو 2017 .\n" +
                        "القانون رقم 99 - 15 : يتعلق بإحداث نظام المعاشات لفائدة فئات المهنيين والعمال المستقلين والأشخاص غير الأجراء الذين يزاولون نشاطا خاصا.\n" +
                        "القانون الإطار رقم 09.21 المتعلق بالحماية الاجتماعية.\n" +
                        "المرسوم رقم 2.21.1019 القاضي بتطبيق القانون رقم 98.15 المتعلق بنظام التأمين الإجباري الأساسي عن المرض والقانون رقم 99.15 بإحداث نظام للمعاشات، الخاصين بفئات المهنيين والعمال المستقلين والأشخاص غير الأجراء الذين يزاولون نشاطا خاصا، فيما يتعلق بالفلاحين.",
                "", clickableWordsps, coloredlinesps));

        //set the adapter to show the content
        adapter = new ExpandableAdapter(getContext(), items);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Access the MainActivity
        Activity_main mainActivity = (Activity_main) requireActivity();
        // Hide the bottom app bar
        mainActivity.hideBottomAppBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Access the MainActivity
        Activity_main mainActivity = (Activity_main) requireActivity();
        // Hide the bottom app bar
        mainActivity.showBottomAppBar();
    }
}