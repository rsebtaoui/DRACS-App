    package com.example.DRACS;

    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.drawable.BitmapDrawable;
    import android.graphics.drawable.Drawable;
    import android.os.Bundle;

    import androidx.fragment.app.Fragment;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import java.util.ArrayList;
    import java.util.List;

    public class RNA extends Fragment {

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

            List<Item> items = new ArrayList<>();
            items.add(new Item("  ▼  الامتيازات:", "<ul>" +
                    "<li><b> الاستفادة من الإعانات المقدمة من طرف وزارة الفلاحة </b></li>" +
                    "<li><b> الاعتراف القانوني بالاستغلالية الفلاحية وإمكانية تتبعها:</b> مما يُسهل عليهم الوصول إلى مختلف الخدمات والفرص المتاحة </li>" +
                    "<li><b>تسهيل الوصول إلى المساعدات والبرامج المقدمة من وزارة الفلاحة:</b> برامج التدريب والدعم والاستشارة التقنية الفلاحية</li>" +
                    "<li><b>تسهيل الولوج إلى التمويل والحماية الاجتماعية لتحسن الظروف المعيشية للفلاحين</b> </li>" +
                    "</ul>"));

            items.add(new Item("  ▼  الوثائق المطلوبة للتسجيل:","<h4>تجدر الإشارة الى ان التسجيل في السجل الوطني الفلاحي يخص المستغلين الذاتيين والمستغلين الاعتبارين أو التنظيمات مثل التعاونيات الفلاحية والشركات الفلاحية</h4>" +
                    "<ol>"+
                    "<li><b>تعبئة استمارة طلب التسجيل الاستغلالية الفلاحية في السجل الوطني الفلاحية (معلومات عن الاستغلالية الفلاحية والنشاط الفلاحي والوسائل المسخرة)</b></li>" +
                    "<li><b>نسخة من البطاقة الوطنية للتعريف الإلكترونية للمستغل الذاتي</b></li>" +
                    "-----> بالنسبة للأجانب: نسخة من سند الإقامة؛" +
                    "<li><b>بالنسبة للتنظيمات: نسخة من البطاقة الوطنية للتعريف الإلكترونية للممثل القانوني للشخص الاعتباري عند الاقتضاء؛</b></li>" +
                    "<li><b>تسمية الشخص الاعتباري وطبيعته ومقره الاجتماعي؛</b></li>" +
                    "<li><b>وثيقة تثبت الطبيعة القانونية للعقار موضوع الاستغلالية؛<b></li>" +
                    "<li><b>الإحداثيات الجغرافية للبقع الفلاحية للضيعة الفلاحية ومساحتها.<b></li>" +
                    "<li><b>نسخة من البطاقة الوطنية للتعريف الإلكترونية للمستغل الذاتي<b></li>" +
                    "</ol>"+
                    "<h4> 'يتسلم المستغل 'وثيقة المعلومات الخاصة باستغلاليته" +
                    "وتتوفر الإدارة على اجل أقصاه 30 يومًا لفحص الملف والتحقق منه" +
                    "إذا كانت المعلومات متوافقة، يتلقى المستغل شهادة التسجيل في السجل الوطني الفلاحي." +
                    "إذا كانت المعلومات غير متوافقة أو غير دقيقة، فلدى المستغل 45 يومًا لتصحيح طلبه وإعادة تقديمه</h4>"));



            items.add(new Item("  ▼  مراكز التسجيل:","<h4>يمكن تقديم طلبات التسجيل والتحيين والتشطيب على مستوى:</h4>"+
                    "<ol>"+
                    "<li><b>المديريات الإقليمية للفلاحة ( (DPA: الجديدة، البيضاء، بنسليمان، سطات، برشيد)<b></li>"+
                    "<li><b>والمقاطعات التابعة للمكتب الجهوي للاستثمار الفلاحي لدكالة (ORMVAD) : سيدي بنور، خميس الزمامرة، أولاد فرج، الغربية<b></li>"+
                    "</ol>" ));

            items.add(new Item("  ▼  تحيين المعلومات:","<h4>يسمح للمشغل القيام بتعديل المعلومات مع تقديم جميع الوثائق التي تثبت صحة المعلومات المدلى بها في غضون 3 أشهر<h4>"+
                    "<ol>"+
                    "<li><b>تعبئة استمارة طلب تحيين المعطيات التي سبق التصريح بها<b></li>"+
                    "</ol>"));

            items.add(new Item("  ▼  التشطيب من السجل:","<h4>يأتي تشطيب الاستغلالية الفلاحية بعد توقف النشاط الفلاحي، بناء على طلب المستغل الفلاحي</h4>" +
                    "<ol>"+
                    "<li><b>تعبئة استمارة طلب التشطيب على تقييد الاستغلالية الفلاحية</li></b>"+
                    "</ol>"));

            items.add(new Item("  ▼  الإطار القانوني المنظم:","<ol>"+
                    "<li><b>الظهير رقم 36-22-1 بتاريخ 24 ماي 2022 تنفيذا للقانون رقم 80.21 بإحداث السجل الوطني الفلاحي<b></li>"+
                    "<li><b>المرسوم رقم 2-22-472 بتاريخ 3 غشت 2022 المتعلق بتطبيق قانون السجل الوطني الفلاحي<b></li>"+
                    "<li><b>والقرار الوزاري رقم 22-2139 بتاريخ 23 مايو 2023 الذي يحدد نماذج طلبات تقييد الاستغلاليات الفلاحية في السجل الوطني الفلاحي، وتحين المعطيات، والتشطيب على التقييد، وكذا نموذج شهادة التقييد.<b></li>"+
                    "</ol>" ));

            items.add(new Item("  ▼  أسئلة وأجوبة:","" +
                    "<h5><font color=\"#FF21AE27\">هل التسجيل في السجل الوطني الفلاحي إلزامي للاستفادة من إعانات وزارة الفلاحة؟</font></h5>"+
                    "--> نعم، للاستفادة من الإعانات المقدمة من طرف وزارة الفلاحة، يجب عى المستغل الفلاحي تسجيل إستغلاليته في السجل الوطني الفلاحي"+
                    "<h5><font color=\"#FF21AE27\">هل يستطيع وكيل المستغل الفلاحي التسجيل بدلاً من المستغل الفلاحي نفسه؟</font></h5>"+
                    "تسجل كل استغلالية فلاحية في السجل الوطني الفلاحي من طرف المستغل الفلاحي أو وكيله المعتمد بناء على طلب مقدم إلى الإدارة."+
                    "<h5><font color=\"#FF21AE27\">ما هي العقوبات القانونية في حالة الإدلاء ببيانات كاذبة؟</font></h5>"+
                    "<ol>"+
                    "</li>يعاقب بغرامة من 5000 إلى 20000 درهم كل مستغل أدلى بسوء نية بتصريح كاذب يخص المعطيات المتعلقة بالاستغلالية<li>" +
                    "</li>الفلاحية في السجل الوطي الفلاحي. وترفع الغرامة إلى عرة ) 10 ( أضعاف إذا كان المستغل شخصا اعتباريا.<li>"+
                    "</ol>"+
                    "<h5><font color=\"#FF21AE27\">هل يؤثر التسجيل في السجل الوطني الفلاحي على ملكية الأراضي الفلاحية؟</font></h5>"+
                    "لا يؤثر التسجيل في السجل الوطني الفلاحي على ملكية الأراضي الفلاحية."+
                    "<h5><font color=\"#FF21AE27\">هل هناك علاقة بين السجل الوطني الفلاحي والضرائب؟</font></h5>"+
                    "<ol>"+
                    "</li>السجل الوطي الفلاحي هو آلية سيتم استخدامها حصريًا في التنمية الفلاحية ولا ترتبط بالضريبة الفلاحية كما تقع مسؤولية<li>"+
                    "</li>الضرائب الفلاحية عى عاتق المديرية العامة للضرائب وليس عى عاتق وزارة الفلاحة. كما يحظر قانون حماية الأشخاص الذاتين<li>"+
                    "</li>اتجاه معالجة المعطيات ذات الطابع الشخصي استخدام هذه البيانات خارج إطار التنمية الفلاحية.<li>"+
                    "</ol>"));

            adapter = new ExpandableAdapter(items);
            recyclerView.setAdapter(adapter);


            return view;
        }
    }
