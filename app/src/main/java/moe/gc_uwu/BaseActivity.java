package moe.gc_uwu;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int mode = (getIntent().getExtras() != null) ? getIntent().getExtras().getInt("mode") : -1;

        if (id == R.id.nav_global) {
            Intent intent = new Intent(this, GlobalRankActivity.class);
            intent.putExtra("mode", 0);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(this, MonthlyRankActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_area) {
            Intent intent = new Intent(this, GlobalRankActivity.class);
            intent.putExtra("mode", 1);
            startActivity(intent);
        } else if(id == R.id.nav_stat) {
            if(mode != 0){
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode",0);
                startActivity(intent);
            }
        } else if (id == R.id.nav_score) {
            if(mode != 1){
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        } else if (id == R.id.nav_monthly_stat) {
            Intent intent = new Intent(this, MonthlyStatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            if(mode != 2) {
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode", 2);
                startActivity(intent);
            }
        } else if (id == R.id.nav_friend) {
            if(mode != 3) {
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode", 3);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
