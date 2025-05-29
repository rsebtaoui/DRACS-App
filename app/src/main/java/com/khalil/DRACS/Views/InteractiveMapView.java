package com.khalil.DRACS.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.khalil.DRACS.R;

public class InteractiveMapView extends AppCompatImageView {
    private OnMapRegionClickListener listener;
    private Drawable redPinDrawable;
    private Drawable bluePinDrawable;

    private static final Region[] REGIONS = {
        // Sidi Bennour (centered pin)
//        new Region(new PointF(0.28f, 0.83f), 0.07f, "Sidi Bennour", 33.6090340,-7.1259160),
        // El Jadida (red pin)
        new Region(new PointF(0.22f, 0.59f), 0.07f, "المديرية الإقليمية للفلاحة للجديدة", 33.24792567109608, -8.502301629715236, true),
        // El Jadida (blue pin)
        new Region(new PointF(0.24f, 0.49f), 0.07f, "المديرية الجهوية للفلاحة للدارالبيضاء - سطات ", 33.24382524367248, -8.494241677080327, false),
        // Casablanca
        new Region(new PointF(0.605f, 0.395f), 0.07f, "المديرية الإقليمية للفلاحة للدار البيضاء", 33.5941745 ,-7.6009861),
        // Berrechid
        new Region(new PointF(0.52f, 0.42f), 0.07f, "المديرية الإقليمية للفلاحة لبرشيد", 33.264458, -7.581894),
        // Settat
        new Region(new PointF(0.71f, 0.76f), 0.07f, "المديرية الإقليمية للفلاحة سطات", 33.0100280, -7.6162690),
        // Ben Slimane
        new Region(new PointF(0.79f, 0.23f), 0.07f, "المديرية الإقليمية للفلاحة بن سليمان", 33.60829239870801, -7.125387907097919)
    };

    public InteractiveMapView(Context context) {
        super(context);
        init();
    }

    public InteractiveMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InteractiveMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        // Load the pin icons
        redPinDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map_pin);
        bluePinDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map_pin_blue);
        if (redPinDrawable != null) {
            redPinDrawable.setBounds(0, 0, redPinDrawable.getIntrinsicWidth(), redPinDrawable.getIntrinsicHeight());
        }
        if (bluePinDrawable != null) {
            bluePinDrawable.setBounds(0, 0, bluePinDrawable.getIntrinsicWidth(), bluePinDrawable.getIntrinsicHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw pins for each region
        for (Region region : REGIONS) {
            // Calculate pixel position for the pin center
            float centerX = region.center.x * getWidth();
            float centerY = region.center.y * getHeight();
            
            // Save canvas state
            canvas.save();
            
            // Get the appropriate pin drawable
            Drawable pinDrawable = region.isRedPin ? redPinDrawable : bluePinDrawable;
            
            // For El Jadida region, adjust the position of the red pin to be slightly offset
            if (region.name.equals("El Jadida") && region.isRedPin) {
                // Offset the red pin slightly to the right
                centerX += 20;
            }
            
            // Translate to pin position (adjust for pin height)
            canvas.translate(centerX - pinDrawable.getIntrinsicWidth() / 2,
                          centerY - pinDrawable.getIntrinsicHeight());
            
            // Draw the pin
            pinDrawable.draw(canvas);
            
            // Restore canvas state
            canvas.restore();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Convert touch coordinates to relative values (0.0 to 1.0)
            float x = event.getX() / getWidth();
            float y = event.getY() / getHeight();

            // Check if touch is within any region (circle)
            for (Region region : REGIONS) {
                if (region.contains(x, y, getWidth())) {
                    openLocationInGoogleMaps(region);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void openLocationInGoogleMaps(Region region) {
        try {
            // Format the Google Maps URI with the location coordinates and name
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)",
                    region.latitude, region.longitude,
                    region.latitude, region.longitude,
                    region.name);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            getContext().startActivity(intent);
        } catch (Exception e) {
            // If Google Maps is not installed, open in browser
            String url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f",
                    region.latitude, region.longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(intent);
        }
    }

    public void setOnMapRegionClickListener(OnMapRegionClickListener listener) {
        this.listener = listener;
    }

    public interface OnMapRegionClickListener {
        void onRegionClick(int actionId);
    }

    // Updated Region class for circle-based clickable area
    private static class Region {
        PointF center;
        float radius; // as a fraction of image width
        String name;
        double latitude;
        double longitude;
        boolean isRedPin;

        Region(PointF center, float radius, String name, double latitude, double longitude) {
            this(center, radius, name, latitude, longitude, true);
        }

        Region(PointF center, float radius, String name, double latitude, double longitude, boolean isRedPin) {
            this.center = center;
            this.radius = radius;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.isRedPin = isRedPin;
        }

        // Check if (x, y) is within the circle (x, y are relative 0.0-1.0, width is in px)
        boolean contains(float x, float y, int widthPx) {
            float dx = (x - center.x) * widthPx;
            float dy = (y - center.y) * widthPx; // use width for both to keep circle aspect
            float distSq = dx * dx + dy * dy;
            float rPx = radius * widthPx;
            return distSq <= rPx * rPx;
        }
    }
} 