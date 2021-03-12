package com.hpx.humanpowerexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ServiceProviders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_providers);
    }
    public void Beautitions(View v){
        Intent i=new Intent(this,BeautitionsPage.class);
        startActivity(i);
    }
    public void Carpenter(View v){
        Intent i=new Intent(this,CarpenterPage.class);
        startActivity(i);
    }
    public void Carttaker(View v){
        Intent i=new Intent(this,CartTaker.class);
        startActivity(i);
    }
    public void Chef(View v){
        Intent i=new Intent(this,ChefPage.class);
        startActivity(i);
    }

    public void Cleaner(View V) {
        Intent i = new Intent(this, CleanerPage.class);
        startActivity(i);
    }
    public void GameCoach(View V) {
        Intent i = new Intent(this, GameCoach.class);
        startActivity(i);
    }
    public void Driver(View V) {
        Intent i = new Intent(this, DriverPage.class);
        startActivity(i);
    }
    public void Designer(View V) {
        Intent i = new Intent(this, DesignerPage.class);
        startActivity(i);
    }
    public void Electrician(View V) {
        Intent i = new Intent(this, EelectricianPage.class);
        startActivity(i);
    }
    public void Gardener(View V) {
        Intent i = new Intent(this, GardenersPage.class);
        startActivity(i);
    }
    public void HomeTutors(View V) {
        Intent i = new Intent(this, HomeTutors.class);
        startActivity(i);
    }
    public void Maidservant(View V) {
        Intent i = new Intent(this, MaidServant.class);
        startActivity(i);
    }
    public void Mason(View V) {
        Intent i = new Intent(this, MasonPage.class);
        startActivity(i);
    }
    public void Mechanic(View V) {
        Intent i = new Intent(this, MechanicPage.class);
        startActivity(i);
    }
    public void Painter(View V) {
        Intent i = new Intent(this, PainterPage.class);
        startActivity(i);
    }
    public void Photographer(View V) {
        Intent i = new Intent(this, Photographer.class);
        startActivity(i);
    }
    public void Plumber(View V) {
        Intent i = new Intent(this, PlumberPage.class);
        startActivity(i);
    }
    public void SocialWorker(View V) {
        Intent i = new Intent(this, SocialWorker.class);
        startActivity(i);
    }
    public void Tailor(View V) {
        Intent i = new Intent(this, Tailor.class);
        startActivity(i);
    }
    public void Watchmen(View V) {
        Intent i = new Intent(this, Watchmen.class);
        startActivity(i);
    }
    public void Welder(View V) {
        Intent i = new Intent(this, WelderPage.class);
        startActivity(i);
    }


}