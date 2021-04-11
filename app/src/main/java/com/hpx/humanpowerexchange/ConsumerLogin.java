package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConsumerLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_login2);
    }
    public void Beautitions(View V){
        Intent i=new Intent(this,CBeautitionPage.class);
        startActivity(i);
    }
    public void Carpenter(View V) {
        Intent i = new Intent(this, CCarpenter.class);
        startActivity(i);
    }
    public void Caretaker(View V){
        Intent i=new Intent(this,CCarpenter.class);
        startActivity(i);
    }
    public void Chef(View V){
        Intent i=new Intent(this,CChef.class);
        startActivity(i);
    }
    public void Cleaner(View V){
        Intent i=new Intent(this,CCleaner.class);
        startActivity(i);
    }
    public void GameCoach(View V){
        Intent i=new Intent(this,CGamecoach.class);
        startActivity(i);

    }
    public void Driver(View V){
        Intent i=new Intent(this,CDriver.class);
        startActivity(i);
    }
    public void Designer(View V){
        Intent i=new Intent(this,CDesigner.class);
        startActivity(i);
    }
    public void Electrician(View V){
        Intent i=new Intent(this,CElectrician.class);
        startActivity(i);
    }

    public void Gardener(View V){
        Intent i=new Intent(this,CGardener.class);
        startActivity(i);
    }
    public void HomeTutors(View V){
        Intent i=new Intent(this,CHometutors.class);
        startActivity(i);
        }
    public void Maidservant(View V){
        Intent i=new Intent(this,CMaidservant.class);
        startActivity(i);
    }
    public void Mason(View V){
        Intent i=new Intent(this,CMason.class);
        startActivity(i);
    }
    public void Mechanic(View V){
        Intent i=new Intent(this,CMechanic.class);
        startActivity(i);
    }
    public void Painter(View V){
        Intent i=new Intent(this,CPainter.class);
        startActivity(i);
    }
    public void Photographer(View V){
        Intent i=new Intent(this,CPhotographer.class);
        startActivity(i);
    }
    public void Plumber(View V){
        Intent i=new Intent(this,CPlumber.class);
        startActivity(i);
    }
    public void SocialWorker(View V){
        Intent i=new Intent(this, CSocialWorkers.class);
        startActivity(i);
    }
    public void Tailor(View V){
        Intent i=new Intent(this,CTailor.class);
        startActivity(i);
    }
    public void Watchmen(View V){
        Intent i=new Intent(this,CWatchmen.class);
        startActivity(i);
    }
    public void Welder(View V){
        Intent i=new Intent(this,CWelder.class);


        startActivity(i);
    }
    }















































































