# DRACS Mobile App - Android Implementation Guide

## Design Specification & XML Layout Guide

This guide provides complete specifications and Android XML code snippets for implementing the DRACS mobile dashboard in native Android.

---

## 1. Design System

### Color Palette

```xml

<!-- res/values/colors.xml -->

<resources>

    <!-- Primary Green System -->

    <color name="primary">#1B5E20</color>

    <color name="primary_dark">#0D3A12</color>

    <color name="primary_light">#2E7D32</color>

    <color name="primary_50">#E8F5E9</color>

    <color name="primary_100">#C8E6C9</color>

    <color name="primary_200">#A5D6A7</color>

    

    <!-- Backgrounds -->

    <color name="background">#FAFAF8</color>

    <color name="background_dark">#0F0F0D</color>

    <color name="card">#FFFFFF</color>

    <color name="card_dark">#1A1A18</color>

    

    <!-- Text Colors -->

    <color name="foreground">#1A1A18</color>

    <color name="foreground_light">#F5F5F3</color>

    <color name="muted_foreground">#6B6B68</color>

    

    <!-- Borders -->

    <color name="border">#E5E5E2</color>

    <color name="border_dark">#2A2A27</color>

    

    <!-- Service Card Colors -->

    <color name="service_blue">#E3F2FD</color>

    <color name="service_blue_accent">#2196F3</color>

    <color name="service_green">#E8F5E9</color>

    <color name="service_green_accent">#4CAF50</color>

    <color name="service_amber">#FFF8E1</color>

    <color name="service_amber_accent">#FFC107</color>

    <color name="service_purple">#F3E5F5</color>

    <color name="service_purple_accent">#9C27B0</color>

</resources>

```

### Typography

```xml

<!-- res/values/dimens.xml -->

<dimen name="text_headline_1">32sp</dimen>

<dimen name="text_headline_2">24sp</dimen>

<dimen name="text_headline_3">20sp</dimen>

<dimen name="text_body">16sp</dimen>

<dimen name="text_small">14sp</dimen>

<dimen name="text_tiny">12sp</dimen>

<!-- Line Heights -->

<dimen name="line_height_tight">1.3</dimen>

<dimen name="line_height_relaxed">1.6</dimen>

<!-- Spacing Scale (16dp base) -->

<dimen name="spacing_xs">4dp</dimen>

<dimen name="spacing_sm">8dp</dimen>

<dimen name="spacing_md">16dp</dimen>

<dimen name="spacing_lg">24dp</dimen>

<dimen name="spacing_xl">32dp</dimen>

<!-- Corner Radius -->

<dimen name="radius_sm">8dp</dimen>

<dimen name="radius_md">12dp</dimen>

<dimen name="radius_lg">16dp</dimen>

<dimen name="radius_xl">24dp</dimen>

<!-- Tap Target (Minimum 44x44 dp) -->

<dimen name="touch_target">44dp</dimen>

<!-- Screen Constraints -->

<dimen name="max_width">390dp</dimen>

<dimen name="header_height">64dp</dimen>

<dimen name="bottom_nav_height">56dp</dimen>

</dimen>

```

---

## 2. Screen Architectures & XML Layouts

### Screen 1: Splash Screen

**Viewport**: 390×844 (full screen)

**Duration**: 2-3 seconds auto-advance

```xml

<!-- splash_activity.xml -->

<?xml version="1.0" encoding="utf-8"?>

<FrameLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="match_parent"

    android:background="@drawable/gradient_primary_dark">

    <LinearLayout

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:orientation="vertical"

        android:gravity="center"

        android:padding="24dp">

        <!-- Logo Circle -->

        <FrameLayout

            android:layout_width="96dp"

            android:layout_height="96dp"

            android:layout_marginBottom="24dp"

            android:background="@drawable/circle_white"

            android:gravity="center">

            

            <TextView

                android:layout_width="wrap_content"

                android:layout_height="wrap_content"

                android:text="د"

                android:textSize="36sp"

                android:textColor="@color/primary"

                android:fontFamily="@font/amiri_bold"

                android:gravity="center" />

        </FrameLayout>

        <!-- Title -->

        <TextView

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:text="المديرية الجهوية"

            android:textSize="@dimen/text_headline_1"

            android:textColor="#FFFFFF"

            android:fontFamily="@font/amiri_bold"

            android:gravity="center"

            android:layout_marginBottom="8dp" />

        <!-- Subtitle -->

        <TextView

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:text="للفلاحة لجهة الدار البيضاء - سطات"

            android:textSize="@dimen/text_body"

            android:textColor="@color/primary_50"

            android:fontFamily="@font/cairo"

            android:gravity="center"

            android:layout_marginBottom="48dp" />

        <!-- Loading Spinner -->

        <ProgressBar

            android:layout_width="48dp"

            android:layout_height="48dp"

            android:layout_marginBottom="48dp"

            android:indeterminate="true"

            android:indeterminateTint="#FFFFFF" />

        <!-- Start Button -->

        <Button

            android:id="@+id/start_button"

            android:layout_width="wrap_content"

            android:layout_height="44dp"

            android:text="ابدأ / Commencer"

            android:paddingStart="32dp"

            android:paddingEnd="32dp"

            android:textColor="@color/primary"

            android:textSize="@dimen/text_body"

            android:fontFamily="@font/cairo_bold"

            android:background="@drawable/button_white_rounded" />

    </LinearLayout>

    <!-- Footer -->

    <TextView

        android:layout_width="match_parent"

        android:layout_height="wrap_content"

        android:text="منصة الخدمات الفلاحية المتكاملة"

        android:textSize="@dimen/text_tiny"

        android:textColor="@color/primary_50"

        android:gravity="center"

        android:layout_gravity="bottom"

        android:paddingBottom="32dp" />

</FrameLayout>

```

---

### Screen 2: Language Selection

```xml

<!-- language_selection_fragment.xml -->

<?xml version="1.0" encoding="utf-8"?>

<LinearLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="match_parent"

    android:orientation="vertical"

    android:background="@drawable/gradient_primary_dark"

    android:gravity="center"

    android:padding="24dp">

    <TextView

        android:layout_width="wrap_content"

        android:layout_height="wrap_content"

        android:text="اختر اللغة / Choisissez la langue"

        android:textSize="@dimen/text_body"

        android:textColor="#FFFFFF"

        android:fontFamily="@font/cairo_medium"

        android:gravity="center"

        android:layout_marginBottom="32dp" />

    <!-- Arabic Button -->

    <Button

        android:id="@+id/lang_arabic"

        android:layout_width="match_parent"

        android:layout_height="44dp"

        android:text="العربية"

        android:textSize="@dimen/text_body"

        android:textColor="#FFFFFF"

        android:fontFamily="@font/cairo_bold"

        android:background="@drawable/button_white_rounded"

        android:layout_marginBottom="16dp" />

    <!-- French Button -->

    <Button

        android:id="@+id/lang_french"

        android:layout_width="match_parent"

        android:layout_height="44dp"

        android:text="Français"

        android:textSize="@dimen/text_body"

        android:textColor="@color/primary"

        android:fontFamily="@font/cairo_bold"

        android:background="@drawable/button_light_rounded" />

</LinearLayout>

```

---

### Screen 3: Dashboard (Main Screen)

```xml

<!-- dashboard_fragment.xml -->

<?xml version="1.0" encoding="utf-8"?>

<CoordinatorLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="match_parent"

    android:background="@color/background">

    <!-- NestedScrollView for content -->

    <NestedScrollView

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:layout_above="@id/bottom_nav"

        android:paddingBottom="@dimen/bottom_nav_height">

        <LinearLayout

            android:layout_width="match_parent"

            android:layout_height="wrap_content"

            android:orientation="vertical"

            android:paddingStart="@dimen/spacing_md"

            android:paddingEnd="@dimen/spacing_md"

            android:paddingTop="@dimen/spacing_md">

            <!-- Regional Map Card (200dp height) -->

            <CardView

                android:layout_width="match_parent"

                android:layout_height="200dp"

                android:layout_marginBottom="@dimen/spacing_md"

                app:cardCornerRadius="@dimen/radius_lg"

                app:cardBackgroundColor="@color/card"

                app:cardElevation="2dp">

                

                <LinearLayout

                    android:layout_width="match_parent"

                    android:layout_height="match_parent"

                    android:orientation="vertical"

                    android:padding="@dimen/spacing_md">

                    

                    <TextView

                        android:layout_width="wrap_content"

                        android:layout_height="wrap_content"

                        android:text="الجهات الإقليمية"

                        android:textSize="@dimen/text_body"

                        android:textColor="@color/primary"

                        android:fontFamily="@font/cairo_bold"

                        android:layout_marginBottom="@dimen/spacing_sm" />

                    

                    <!-- Map SVG or Image View -->

                    <ImageView

                        android:id="@+id/regional_map"

                        android:layout_width="match_parent"

                        android:layout_height="0dp"

                        android:layout_weight="1"

                        android:src="@drawable/map_casablanca_settat"

                        android:scaleType="centerInside" />

                </LinearLayout>

            </CardView>

            <!-- Welcome Message -->

            <CardView

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                android:layout_marginBottom="@dimen/spacing_md"

                app:cardCornerRadius="@dimen/radius_lg"

                app:cardBackgroundColor="@color/primary_50"

                app:cardElevation="0dp">

                

                <TextView

                    android:layout_width="match_parent"

                    android:layout_height="wrap_content"

                    android:text="مرحبا بك في منصة الخدمات الفلاحية المتكاملة"

                    android:textSize="@dimen/text_small"

                    android:textColor="@color/primary"

                    android:fontFamily="@font/cairo_medium"

                    android:padding="@dimen/spacing_md" />

            </CardView>

            <!-- Services Grid 2x2 -->

            <GridLayout

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                android:columnCount="2"

                android:rowCount="2"

                android:layout_marginBottom="@dimen/spacing_md">

                <!-- Service Card 1 (Blue) -->

                <CardView

                    android:layout_width="0dp"

                    android:layout_height="120dp"

                    android:layout_row="0"

                    android:layout_column="0"

                    android:layout_columnWeight="1"

                    android:layout_marginEnd="@dimen/spacing_sm"

                    android:layout_marginBottom="@dimen/spacing_sm"

                    app:cardCornerRadius="@dimen/radius_lg"

                    app:cardBackgroundColor="@color/service_blue"

                    app:cardElevation="1dp">

                    <LinearLayout

                        android:layout_width="match_parent"

                        android:layout_height="match_parent"

                        android:orientation="vertical"

                        android:padding="@dimen/spacing_md">

                        <FrameLayout

                            android:layout_width="match_parent"

                            android:layout_height="wrap_content"

                            android:layout_marginBottom="@dimen/spacing_sm">

                            

                            <TextView

                                android:layout_width="wrap_content"

                                android:layout_height="wrap_content"

                                android:text="📋"

                                android:textSize="28sp" />

                            

                            <ImageButton

                                android:id="@+id/fav_registry"

                                android:layout_width="32dp"

                                android:layout_height="32dp"

                                android:layout_gravity="end"

                                android:background="@drawable/button_heart"

                                android:contentDescription="@string/favorite" />

                        </FrameLayout>

                        <TextView

                            android:layout_width="match_parent"

                            android:layout_height="wrap_content"

                            android:text="السجل الفلاحي الوطني"

                            android:textSize="@dimen/text_small"

                            android:textColor="#333333"

                            android:fontFamily="@font/cairo_bold"

                            android:layout_marginBottom="4dp" />

                        <TextView

                            android:layout_width="match_parent"

                            android:layout_height="wrap_content"

                            android:text="معلومات السجل الشامل"

                            android:textSize="@dimen/text_tiny"

                            android:textColor="#666666"

                            android:fontFamily="@font/cairo" />

                    </LinearLayout>

                </CardView>

                <!-- Service Card 2 (Green) -->

                <CardView

                    android:layout_width="0dp"

                    android:layout_height="120dp"

                    android:layout_row="0"

                    android:layout_column="1"

                    android:layout_columnWeight="1"

                    android:layout_marginStart="@dimen/spacing_sm"

                    android:layout_marginBottom="@dimen/spacing_sm"

                    app:cardCornerRadius="@dimen/radius_lg"

                    app:cardBackgroundColor="@color/service_green"

                    app:cardElevation="1dp">

                    <!-- Similar structure to Card 1, with green accent -->

                </CardView>

                <!-- Service Card 3 (Amber) -->

                <CardView

                    android:layout_width="0dp"

                    android:layout_height="120dp"

                    android:layout_row="1"

                    android:layout_column="0"

                    android:layout_columnWeight="1"

                    android:layout_marginEnd="@dimen/spacing_sm"

                    android:layout_marginTop="@dimen/spacing_sm"

                    app:cardCornerRadius="@dimen/radius_lg"

                    app:cardBackgroundColor="@color/service_amber"

                    app:cardElevation="1dp">

                    <!-- Similar structure -->

                </CardView>

                <!-- Service Card 4 (Purple) -->

                <CardView

                    android:layout_width="0dp"

                    android:layout_height="120dp"

                    android:layout_row="1"

                    android:layout_column="1"

                    android:layout_columnWeight="1"

                    android:layout_marginStart="@dimen/spacing_sm"

                    android:layout_marginTop="@dimen/spacing_sm"

                    app:cardCornerRadius="@dimen/radius_lg"

                    app:cardBackgroundColor="@color/service_purple"

                    app:cardElevation="1dp">

                    <!-- Similar structure -->

                </CardView>

            </GridLayout>

            <!-- Instruction Card -->

            <CardView

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                app:cardCornerRadius="@dimen/radius_lg"

                app:cardBackgroundColor="@color/card"

                app:cardElevation="0dp">

                

                <TextView

                    android:layout_width="match_parent"

                    android:layout_height="wrap_content"

                    android:text="اضغط على أي خدمة لمزيد من التفاصيل"

                    android:textSize="@dimen/text_tiny"

                    android:textColor="@color/foreground"

                    android:gravity="center"

                    android:fontFamily="@font/cairo_medium"

                    android:padding="@dimen/spacing_md" />

            </CardView>

        </LinearLayout>

    </NestedScrollView>

</CoordinatorLayout>

```

---

### Screen 4: Service Details / Expandable Content

```xml

<!-- service_detail_fragment.xml -->

<?xml version="1.0" encoding="utf-8"?>

<LinearLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="match_parent"

    android:orientation="vertical"

    android:background="@color/background">

    <!-- Toolbar with back button -->

    <FrameLayout

        android:layout_width="match_parent"

        android:layout_height="@dimen/header_height"

        android:background="@color/primary">

        <Button

            android:id="@+id/back_button"

            android:layout_width="44dp"

            android:layout_height="44dp"

            android:layout_gravity="start|center_vertical"

            android:background="?attr/selectableItemBackgroundBorderless"

            android:contentDescription="@string/back" />

    </FrameLayout>

    <!-- Content -->

    <NestedScrollView

        android:layout_width="match_parent"

        android:layout_height="0dp"

        android:layout_weight="1"

        android:paddingStart="@dimen/spacing_md"

        android:paddingEnd="@dimen/spacing_md"

        android:paddingTop="@dimen/spacing_md"

        android:paddingBottom="@dimen/spacing_lg">

        <LinearLayout

            android:layout_width="match_parent"

            android:layout_height="wrap_content"

            android:orientation="vertical">

            <!-- Title -->

            <TextView

                android:id="@+id/service_title"

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                android:textSize="@dimen/text_headline_2"

                android:textColor="@color/primary"

                android:fontFamily="@font/amiri_bold"

                android:layout_marginBottom="@dimen/spacing_lg" />

            <!-- Content Card -->

            <CardView

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                android:layout_marginBottom="@dimen/spacing_lg"

                app:cardCornerRadius="@dimen/radius_lg"

                app:cardBackgroundColor="@color/card"

                app:cardElevation="1dp">

                <TextView

                    android:id="@+id/service_content"

                    android:layout_width="match_parent"

                    android:layout_height="wrap_content"

                    android:textSize="@dimen/text_body"

                    android:textColor="@color/foreground"

                    android:fontFamily="@font/cairo"

                    android:lineHeight="@dimen/line_height_relaxed"

                    android:padding="@dimen/spacing_lg" />

            </CardView>

            <!-- Action Buttons -->

            <Button

                android:id="@+id/action_button"

                android:layout_width="match_parent"

                android:layout_height="44dp"

                android:text="الاستفادة من الخدمة"

                android:textSize="@dimen/text_body"

                android:textColor="@color/primary_foreground"

                android:fontFamily="@font/cairo_bold"

                android:background="@drawable/button_primary_rounded"

                android:layout_marginBottom="@dimen/spacing_md" />

            <Button

                android:id="@+id/learn_more_button"

                android:layout_width="match_parent"

                android:layout_height="44dp"

                android:text="معرفة المزيد"

                android:textSize="@dimen/text_body"

                android:textColor="@color/primary"

                android:fontFamily="@font/cairo_bold"

                android:background="@drawable/button_secondary_rounded"

                android:layout_marginBottom="@dimen/spacing_lg" />

            <!-- Contact Card -->

            <CardView

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                app:cardCornerRadius="@dimen/radius_lg"

                app:cardBackgroundColor="@color/muted"

                app:cardElevation="0dp">

                <LinearLayout

                    android:layout_width="match_parent"

                    android:layout_height="wrap_content"

                    android:orientation="vertical"

                    android:padding="@dimen/spacing_lg">

                    <TextView

                        android:layout_width="wrap_content"

                        android:layout_height="wrap_content"

                        android:text="للتواصل معنا:"

                        android:textSize="@dimen/text_small"

                        android:textColor="@color/muted_foreground"

                        android:fontFamily="@font/cairo_bold"

                        android:layout_marginBottom="@dimen/spacing_md" />

                    <TextView

                        android:layout_width="wrap_content"

                        android:layout_height="wrap_content"

                        android:text="الهاتف: +212 5 22 95 10 10"

                        android:textSize="@dimen/text_small"

                        android:textColor="@color/foreground"

                        android:fontFamily="@font/cairo" />

                    <TextView

                        android:layout_width="wrap_content"

                        android:layout_height="wrap_content"

                        android:text="البريد: [info@dracs.gov.ma](mailto:info@dracs.gov.ma)"

                        android:textSize="@dimen/text_small"

                        android:textColor="@color/foreground"

                        android:fontFamily="@font/cairo" />

                </LinearLayout>

            </CardView>

        </LinearLayout>

    </NestedScrollView>

</LinearLayout>

```

---

### Screen 5: Favorites

```xml

<!-- favorites_fragment.xml -->

<?xml version="1.0" encoding="utf-8"?>

<LinearLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="match_parent"

    android:orientation="vertical"

    android:background="@color/background">

    <!-- Empty State -->

    <LinearLayout

        android:id="@+id/empty_state"

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:orientation="vertical"

        android:gravity="center"

        android:padding="@dimen/spacing_lg"

        android:visibility="gone">

        <TextView

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:text="📌"

            android:textSize="72sp"

            android:layout_marginBottom="@dimen/spacing_lg"

            android:alpha="0.4" />

        <TextView

            android:layout_width="match_parent"

            android:layout_height="wrap_content"

            android:text="لا توجد خدمات مفضلة بعد"

            android:textSize="@dimen/text_body"

            android:textColor="@color/muted_foreground"

            android:gravity="center"

            android:fontFamily="@font/cairo_bold"

            android:layout_marginBottom="@dimen/spacing_md" />

        <TextView

            android:layout_width="match_parent"

            android:layout_height="wrap_content"

            android:text="اضغط على أيقونة القلب لإضافة خدمات"

            android:textSize="@dimen/text_small"

            android:textColor="@color/muted_foreground"

            android:gravity="center"

            android:fontFamily="@font/cairo" />

    </LinearLayout>

    <!-- Filled State -->

    <NestedScrollView

        android:id="@+id/favorites_list"

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:paddingStart="@dimen/spacing_md"

        android:paddingEnd="@dimen/spacing_md"

        android:paddingTop="@dimen/spacing_md">

        <LinearLayout

            android:layout_width="match_parent"

            android:layout_height="wrap_content"

            android:orientation="vertical">

            <!-- Favorites count card -->

            <CardView

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                android:layout_marginBottom="@dimen/spacing_md"

                app:cardCornerRadius="@dimen/radius_lg"

                app:cardBackgroundColor="@color/card"

                app:cardElevation="1dp">

                <TextView

                    android:id="@+id/favorites_count"

                    android:layout_width="match_parent"

                    android:layout_height="wrap_content"

                    android:textSize="@dimen/text_body"

                    android:textColor="@color/foreground"

                    android:fontFamily="@font/cairo_medium"

                    android:padding="@dimen/spacing_md" />

            </CardView>

            <!-- RecyclerView for favorites -->

            <RecyclerView

                android:id="@+id/favorites_recycler"

                android:layout_width="match_parent"

                android:layout_height="wrap_content"

                android:nestedScrollingEnabled="false" />

        </LinearLayout>

    </NestedScrollView>

</LinearLayout>

```

---

### Screen 6: Settings

```xml

<!-- settings_fragment.xml -->

<?xml version="1.0" encoding="utf-8"?>

<NestedScrollView xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="match_parent"

    android:background="@color/background">

    <LinearLayout

        android:layout_width="match_parent"

        android:layout_height="wrap_content"

        android:orientation="vertical"

        android:padding="@dimen/spacing_md">

        <!-- Language Section -->

        <TextView

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:text="اللغة"

            android:textSize="@dimen/text_headline_3"

            android:textColor="@color/primary"

            android:fontFamily="@font/amiri_bold"

            android:layout_marginBottom="@dimen/spacing_md" />

        <Button

            android:id="@+id/lang_ar_setting"

            android:layout_width="match_parent"

            android:layout_height="44dp"

            android:text="العربية (Arabic)"

            android:textSize="@dimen/text_body"

            android:fontFamily="@font/cairo_bold"

            android:background="@drawable/button_primary_rounded"

            android:layout_marginBottom="@dimen/spacing_sm" />

        <Button

            android:id="@+id/lang_fr_setting"

            android:layout_width="match_parent"

            android:layout_height="44dp"

            android:text="Français (French)"

            android:textSize="@dimen/text_body"

            android:fontFamily="@font/cairo_bold"

            android:background="@drawable/button_secondary_rounded"

            android:layout_marginBottom="@dimen/spacing_lg" />

        <!-- Display Section -->

        <TextView

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:text="العرض"

            android:textSize="@dimen/text_headline_3"

            android:textColor="@color/primary"

            android:fontFamily="@font/amiri_bold"

            android:layout_marginBottom="@dimen/spacing_md" />

        <!-- Dark Mode Toggle -->

        <FrameLayout

            android:layout_width="match_parent"

            android:layout_height="@dimen/touch_target"

            android:background="@drawable/card_rounded"

            android:layout_marginBottom="@dimen/spacing_sm">

            <LinearLayout

                android:layout_width="match_parent"

                android:layout_height="match_parent"

                android:orientation="horizontal"

                android:gravity="center_vertical"

                android:padding="@dimen/spacing_md">

                <ImageView

                    android:layout_width="24dp"

                    android:layout_height="24dp"

                    android:src="@drawable/ic_moon"

                    android:contentDescription="@string/dark_mode"

                    android:layout_marginEnd="@dimen/spacing_md" />

                <TextView

                    android:layout_width="0dp"

                    android:layout_height="wrap_content"

                    android:layout_weight="1"

                    android:text="الوضع الليلي"

                    android:textSize="@dimen/text_body"

                    android:textColor="@color/foreground"

                    android:fontFamily="@font/cairo_medium" />

                <SwitchCompat

                    android:id="@+id/dark_mode_switch"

                    android:layout_width="wrap_content"

                    android:layout_height="wrap_content"

                    android:thumb="@drawable/switch_thumb"

                    android:track="@drawable/switch_track" />

            </LinearLayout>

        </FrameLayout>

        <!-- Large Font Toggle -->

        <FrameLayout

            android:layout_width="match_parent"

            android:layout_height="@dimen/touch_target"

            android:background="@drawable/card_rounded"

            android:layout_marginBottom="@dimen/spacing_lg">

            <LinearLayout

                android:layout_width="match_parent"

                android:layout_height="match_parent"

                android:orientation="horizontal"

                android:gravity="center_vertical"

                android:padding="@dimen/spacing_md">

                <ImageView

                    android:layout_width="24dp"

                    android:layout_height="24dp"

                    android:src="@drawable/ic_text_size"

                    android:contentDescription="@string/large_font"

                    android:layout_marginEnd="@dimen/spacing_md" />

                <TextView

                    android:layout_width="0dp"

                    android:layout_height="wrap_content"

                    android:layout_weight="1"

                    android:text="خط أكبر"

                    android:textSize="@dimen/text_body"

                    android:textColor="@color/foreground"

                    android:fontFamily="@font/cairo_medium" />

                <SwitchCompat

                    android:id="@+id/large_font_switch"

                    android:layout_width="wrap_content"

                    android:layout_height="wrap_content" />

            </LinearLayout>

        </FrameLayout>

        <!-- Accessibility Section -->

        <TextView

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:text="سهولة الاستخدام"

            android:textSize="@dimen/text_headline_3"

            android:textColor="@color/primary"

            android:fontFamily="@font/amiri_bold"

            android:layout_marginBottom="@dimen/spacing_md" />

        <!-- Sound Toggle -->

        <FrameLayout

            android:layout_width="match_parent"

            android:layout_height="@dimen/touch_target"

            android:background="@drawable/card_rounded"

            android:layout_marginBottom="@dimen/spacing_lg">

            <LinearLayout

                android:layout_width="match_parent"

                android:layout_height="match_parent"

                android:orientation="horizontal"

                android:gravity="center_vertical"

                android:padding="@dimen/spacing_md">

                <ImageView

                    android:layout_width="24dp"

                    android:layout_height="24dp"

                    android:src="@drawable/ic_sound"

                    android:contentDescription="@string/sounds"

                    android:layout_marginEnd="@dimen/spacing_md" />

                <TextView

                    android:layout_width="0dp"

                    android:layout_height="wrap_content"

                    android:layout_weight="1"

                    android:text="الأصوات"

                    android:textSize="@dimen/text_body"

                    android:textColor="@color/foreground"

                    android:fontFamily="@font/cairo_medium" />

                <SwitchCompat

                    android:id="@+id/sound_switch"

                    android:layout_width="wrap_content"

                    android:layout_height="wrap_content" />

            </LinearLayout>

        </FrameLayout>

        <!-- Feedback Button -->

        <Button

            android:id="@+id/feedback_button"

            android:layout_width="match_parent"

            android:layout_height="44dp"

            android:text="إرسال ملاحظات"

            android:textSize="@dimen/text_body"

            android:textColor="#FFFFFF"

            android:fontFamily="@font/cairo_bold"

            android:background="@drawable/button_primary_rounded"

            android:layout_marginTop="@dimen/spacing_lg" />

    </LinearLayout>

</NestedScrollView>

```

---

## 3. Material 3 Top App Bar (Toolbar)

```xml

<!-- toolbar_main.xml -->

<?xml version="1.0" encoding="utf-8"?>

<FrameLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="@dimen/header_height"

    android:background="@color/primary">

    <!-- Menu Button (Left - RTL) -->

    <ImageButton

        android:id="@+id/menu_button"

        android:layout_width="44dp"

        android:layout_height="44dp"

        android:layout_gravity="start"

        android:background="?attr/selectableItemBackgroundBorderless"

        android:src="@drawable/ic_menu"

        android:contentDescription="@string/menu" />

    <!-- Title (Center) -->

    <LinearLayout

        android:layout_width="wrap_content"

        android:layout_height="match_parent"

        android:orientation="vertical"

        android:gravity="center"

        android:layout_gravity="center">

        <TextView

            android:id="@+id/toolbar_title"

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:textSize="@dimen/text_headline_3"

            android:textColor="#FFFFFF"

            android:fontFamily="@font/cairo_bold"

            android:text="الرئيسية" />

        <TextView

            android:id="@+id/toolbar_subtitle"

            android:layout_width="wrap_content"

            android:layout_height="wrap_content"

            android:textSize="@dimen/text_tiny"

            android:textColor="@color/primary_50"

            android:fontFamily="@font/cairo"

            android:text="الدار البيضاء - سطات" />

    </LinearLayout>

    <!-- Info Button (Right - RTL) -->

    <ImageButton

        android:id="@+id/info_button"

        android:layout_width="44dp"

        android:layout_height="44dp"

        android:layout_gravity="end"

        android:background="?attr/selectableItemBackgroundBorderless"

        android:src="@drawable/ic_info"

        android:contentDescription="@string/info" />

</FrameLayout>

```

---

## 4. Material 3 Bottom Navigation Bar

```xml

<!-- bottom_nav.xml -->

<?xml version="1.0" encoding="utf-8"?>

<FrameLayout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:layout_width="match_parent"

    android:layout_height="@dimen/bottom_nav_height"

    android:background="@color/card"

    android:elevation="8dp">

    <LinearLayout

        android:layout_width="match_parent"

        android:layout_height="match_parent"

        android:orientation="horizontal">

        <!-- Home Tab -->

        <FrameLayout

            android:id="@+id/nav_home"

            android:layout_width="0dp"

            android:layout_height="match_parent"

            android:layout_weight="1"

            android:background="?attr/selectableItemBackground">

            <LinearLayout

                android:layout_width="wrap_content"

                android:layout_height="wrap_content"

                android:orientation="vertical"

                android:gravity="center"

                android:layout_gravity="center">

                <ImageView

                    android:id="@+id/nav_home_icon"

                    android:layout_width="24dp"

                    android:layout_height="24dp"

                    android:src="@drawable/ic_home"

                    android:contentDescription="@string/home"

                    android:tint="@color/primary" />

                <TextView

                    android:layout_width="wrap_content"

                    android:layout_height="wrap_content"

                    android:text="الرئيسية"

                    android:textSize="@dimen/text_tiny"

                    android:textColor="@color/primary"

                    android:fontFamily="@font/cairo_medium"

                    android:layout_marginTop="4dp" />

            </LinearLayout>

        </FrameLayout>

        <!-- Favorites Tab -->

        <FrameLayout

            android:id="@+id/nav_favorites"

            android:layout_width="0dp"

            android:layout_height="match_parent"

            android:layout_weight="1"

            android:background="?attr/selectableItemBackground">

            <LinearLayout

                android:layout_width="wrap_content"

                android:layout_height="wrap_content"

                android:orientation="vertical"

                android:gravity="center"

                android:layout_gravity="center">

                <ImageView

                    android:id="@+id/nav_favorites_icon"

                    android:layout_width="24dp"

                    android:layout_height="24dp"

                    android:src="@drawable/ic_heart"

                    android:contentDescription="@string/favorites"

                    android:tint="@color/muted_foreground" />

                <TextView

                    android:layout_width="wrap_content"

                    android:layout_height="wrap_content"

                    android:text="المفضلة"

                    android:textSize="@dimen/text_tiny"

                    android:textColor="@color/muted_foreground"

                    android:fontFamily="@font/cairo_medium"

                    android:layout_marginTop="4dp" />

            </LinearLayout>

        </FrameLayout>

        <!-- Settings Tab -->

        <FrameLayout

            android:id="@+id/nav_settings"

            android:layout_width="0dp"

            android:layout_height="match_parent"

            android:layout_weight="1"

            android:background="?attr/selectableItemBackground">

            <LinearLayout

                android:layout_width="wrap_content"

                android:layout_height="wrap_content"

                android:orientation="vertical"

                android:gravity="center"

                android:layout_gravity="center">

                <ImageView

                    android:id="@+id/nav_settings_icon"

                    android:layout_width="24dp"

                    android:layout_height="24dp"

                    android:src="@drawable/ic_settings"

                    android:contentDescription="@string/settings"

                    android:tint="@color/muted_foreground" />

                <TextView

                    android:layout_width="wrap_content"

                    android:layout_height="wrap_content"

                    android:text="الإعدادات"

                    android:textSize="@dimen/text_tiny"

                    android:textColor="@color/muted_foreground"

                    android:fontFamily="@font/cairo_medium"

                    android:layout_marginTop="4dp" />

            </LinearLayout>

        </FrameLayout>

    </LinearLayout>

</FrameLayout>

```

---

## 5. Drawable Resources

### Gradients

```xml

<!-- drawable/gradient_primary_dark.xml -->

<?xml version="1.0" encoding="utf-8"?>

<shape xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:shape="rectangle">

    <gradient

        android:type="linear"

        android:angle="135"

        android:startColor="@color/primary"

        android:endColor="@color/primary_dark" />

</shape>

```

### Button Styles

```xml

<!-- drawable/button_primary_rounded.xml -->

<?xml version="1.0" encoding="utf-8"?>

<shape xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:shape="rectangle">

    <solid android:color="@color/primary" />

    <corners android:radius="@dimen/radius_lg" />

</shape>

<!-- drawable/button_secondary_rounded.xml -->

<?xml version="1.0" encoding="utf-8"?>

<shape xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:shape="rectangle">

    <solid android:color="@color/primary_50" />

    <stroke android:width="2dp" android:color="@color/primary_200" />

    <corners android:radius="@dimen/radius_lg" />

</shape>

<!-- drawable/card_rounded.xml -->

<?xml version="1.0" encoding="utf-8"?>

<shape xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"

    android:shape="rectangle">

    <solid android:color="@color/card" />

    <stroke android:width="1dp" android:color="@color/border" />

    <corners android:radius="@dimen/radius_lg" />

</shape>

```

---

## 6. Fonts

Place these files in `res/font/` directory:

- `cairo.ttf` - Cairo Regular (body text)

- `cairo_bold.ttf` - Cairo Bold (headings, buttons)

- `cairo_medium.ttf` - Cairo Medium (secondary text)

- `amiri_bold.ttf` - Amiri Bold (primary headings, branding)

```xml

<!-- res/font/cairo.xml -->

<?xml version="1.0" encoding="utf-8"?>

<font-family xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)">

    <font android:font="@font/cairo" android:fontStyle="normal" android:fontWeight="400" />

    <font android:font="@font/cairo_bold" android:fontStyle="normal" android:fontWeight="700" />

    <font android:font="@font/cairo_medium" android:fontStyle="normal" android:fontWeight="500" />

</font-family>

```

---

## 7. Dimensions Summary Table

| Element | Size | Notes |

|---------|------|-------|

| **Viewport** | 390×844dp | Standard Android portrait |

| **Top App Bar** | 64dp | Material 3 standard |

| **Bottom Navigation** | 56dp | Material 3 standard |

| **Card Padding** | 16dp (md) | Consistent spacing |

| **Border Radius** | 24dp (xl) | Cards, buttons |

| **Touch Target** | 44×44dp | Minimum accessibility |

| **Icon Size** | 24dp | UI icons |

| **Body Text** | 16sp | Main content |

| **Heading** | 32sp | Titles |

| **Regional Map** | 200dp | Fixed height card |

| **Service Card** | 120dp | Grid item height |

| **Grid Gap** | 12dp | Between cards |

---

## 8. Implementation Checklist

- [ ] Set up color resources

- [ ] Import fonts (Cairo, Amiri)

- [ ] Create all drawable shapes

- [ ] Implement layout XMLs

- [ ] Set up MainActivity with Fragment navigation

- [ ] Implement splash screen flow

- [ ] Build top app bar Toolbar

- [ ] Build bottom navigation

- [ ] Create Service data model

- [ ] Implement favorites system (Room DB or SharedPreferences)

- [ ] Add RTL support

- [ ] Add dark mode support

- [ ] Test on actual devices (360-412px width)

- [ ] Verify touch targets (44×44dp minimum)

- [ ] Test Arabic text rendering

- [ ] Implement animations/transitions

This complete guide provides everything needed to build the DRACS mobile app natively in Android!

