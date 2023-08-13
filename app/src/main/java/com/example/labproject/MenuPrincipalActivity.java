package com.example.labproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MenuPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /*  */
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir_cajon_navegacion,
                R.string.cerrar_cajon_navegacion);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.boton_inicio){
                    abrirFragmento(new FragmentoInicio());
                    return true;
                } else if (itemId == R.id.boton_profesores) {
                    abrirFragmento(new FragmentProfessors());
                    return true;
                } else if (itemId == R.id.boton_alumnos) {
                    abrirFragmento(new FragmentoAlumnos());
                    return true;
                }else if (itemId == R.id.boton_reportes) {
                    abrirFragmento(new FragmentoReportes());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        abrirFragmento(new FragmentoInicio());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuPrincipalActivity.this, "Creando Sesion", Toast.LENGTH_SHORT).show();
                abrirFragmento(new FragmentoSesiones());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        /* aqui se abren cada uno de los fragmento que queremos acceder del lado derecho */
        if(itemId == R.id.encargados){
            // Abrir el fragmento "FragmentoEncargados"
            abrirFragmento(new FragmentoEncargados());
        } else if (itemId == R.id.sesion_individual) {
            abrirFragmento(new FragmentoSesionIndividual());
        } else if (itemId == R.id.sesion_grupal) {
            abrirFragmento(new FragmentoSesionGrupal());
        } else if (itemId == R.id.perfil) {
            abrirFragmento(new FragmentoPerfil());
        } else if (itemId == R.id.ayuda) {
            abrirFragmento(new FragmentoAyuda());
        } else if (itemId == R.id.salir) {
            Toast.makeText(this,"Saliendo" ,Toast.LENGTH_LONG).show();
            /*aqui iria el codigo de cerrar sesion y lo mande a cerrar la sesion*/
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void abrirFragmento(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}