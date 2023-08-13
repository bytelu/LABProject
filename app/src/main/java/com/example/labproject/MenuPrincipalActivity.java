package com.example.labproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

        // Obtener el nombre de usuario guardado en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String usuario = sharedPreferences.getString("usuario", "");

        // Ahora se puede utiliza el nombre de usuario para mostrarlo o hacer querys con el
        // Por ejemplo, mostrarlo en un TextView
        // TextView nombreUsuarioTextView = findViewById(R.id.nombreUsuarioTextView);
        // nombreUsuarioTextView.setText(usuario);

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
                //Toast.makeText(MenuPrincipalActivity.this, "Creando Sesion", Toast.LENGTH_SHORT).show();
                Toast.makeText(MenuPrincipalActivity.this, "Creando sesión para el usuario: " + usuario, Toast.LENGTH_SHORT).show();
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
            // Mostrar cuadro de diálogo de confirmación
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Confirmar");
            builder.setMessage("¿Estás seguro de que deseas cerrar sesión?");
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Borrar datos de SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Borrar todos los datos almacenados
                    editor.apply();

                    // Abrir ActivityInicio
                    Intent intent = new Intent(MenuPrincipalActivity.this, InicioActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar la pila de actividades
                    startActivity(intent);

                    finish(); // Cierra la actividad actual
                }
            });
            builder.setNegativeButton("No",null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.GONE);
                }
            });
            dialog.show();
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