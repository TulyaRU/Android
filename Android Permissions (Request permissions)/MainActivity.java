package com.example.myapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSION = 1234;
    private static final int MULTI_REQUEST_PERMISSIONS = 5678;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Старая логика
        Button btnCameraRequestPermissionsOld = findViewById(R.id.btnRequestPermissionOld);
        btnCameraRequestPermissionsOld.setOnClickListener(view -> {
            if (checkPermission()) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[] {
                                Manifest.permission.CAMERA
                        },
                        REQUEST_PERMISSION
                );
            }
        });

        Button btnMultiRequestPermissionsOld = findViewById(R.id.btnMultiRequestPermissionsOld);
        btnMultiRequestPermissionsOld.setOnClickListener(view -> {
            if (checkMultiPermissions()) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[] {
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_PERMISSION
                );
            }
        });

        //  Новая логика
        Button btnRequestPermissionNew = findViewById(R.id.btnRequestPermissionNew);
        btnRequestPermissionNew.setOnClickListener(view -> {
            if (checkPermission()) {
                openCamera();
            } else {
                requestPermissions.launch(Manifest.permission.CAMERA);
            }
        });

        Button btnMultiRequestPermissionsNew = findViewById(R.id.btnMultiRequestPermissionsNew);
        btnMultiRequestPermissionsNew.setOnClickListener(view -> {
            if (checkMultiPermissions()) {
                openCamera();
            } else {
                multiRequestPermissions.launch(
                        new String[] {
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        });
            }
        });
    }

    //  Метод пустышка, имитируют запуск видеокамеры
    private void openCamera() {
        Toast.makeText(this, "Запуск видеокамеры", Toast.LENGTH_SHORT).show();
    }

    //  Проверка разрешение на видеокамеру
    private boolean checkPermission() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        //  Для получение результат, необходимо сверить переменную с PackageManager.PERMISSION_GRANTED
        return camera == PackageManager.PERMISSION_GRANTED;
    }

    //  Проверка разрешение на видеокамеру и SD хранилищу
    private boolean checkMultiPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writeExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //  Для получение результат, необходимо все переменные сверить с PackageManager.PERMISSION_GRANTED
        return camera == PackageManager.PERMISSION_GRANTED
                && writeExternalStorage == PackageManager.PERMISSION_GRANTED
                && readExternalStorage == PackageManager.PERMISSION_GRANTED;
    }

    /*
    Этот метод является устаревшим!
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {  //
            if (requestCode == REQUEST_PERMISSION) {
                if (permissions[0].equals(Manifest.permission.CAMERA)) {
                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (camera) {
                        Log.d(TAG, "Разрешения к камере предоставлены");
                    } else {
                        Log.d(TAG, "Разрешения к камере не предоставлены");
                    }
                }
            } else if (requestCode == MULTI_REQUEST_PERMISSIONS) {
                boolean camera = false;
                boolean writeExternalStorage = false;
                boolean readExternalStorage = false;
                int permissionCount = permissions.length; //  Получаем количество разрешений
                for (int i = 0; i < permissionCount; i++) {
                    int grantResult = grantResults[i];
                    String permission = permissions[i];
                    switch (permission) { //  Используем оператор switch
                        case Manifest.permission.CAMERA:
                            //  Используем оператор равенство и результат записываем в переменную camera
                            camera = grantResult == PackageManager.PERMISSION_GRANTED;
                            break;
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            //  Используем оператор равенство и результат записываем в переменную writeExternalStorage
                            writeExternalStorage = grantResult == PackageManager.PERMISSION_GRANTED;
                            break;
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                            //  Используем оператор равенство и результат записываем в переменную readExternalStorage
                            readExternalStorage = grantResult == PackageManager.PERMISSION_GRANTED;
                            break;
                    }
                }
                //  Все переменные должны быть true, чтобы соответствовать условию
                if (camera && writeExternalStorage && readExternalStorage) {
                    Log.d(TAG, "Все разрешения предоставлены");
                    openCamera();
                } else {
                    Log.d(TAG, "Не все разрешения предоставлены");
                    Toast.makeText(this, "Не все разрешения предоставлены", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*
   Этот метод является новой альтернативой, старому!
    */

    //  Обработчик для одного разрешение
    private final ActivityResultLauncher<String> requestPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        Log.d(TAG, "Разрешения к камере предоставлены");
                    } else {
                        Log.d(TAG, "Разрешения к камере не предоставлены");
                    }
                }
            });

    //  Обработчик для N количество разрешения
    private final ActivityResultLauncher<String[]> multiRequestPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    //  Переменные для результатов, по умолчанию false
                    boolean camera = false;
                    boolean writeExternalStorage = false;
                    boolean readExternalStorage = false;
                    //  Метод entrySet (result.entrySet()) возвращает нам коллекцию ключ/значение (Key, Value)
                    //  в которой ключ (String) это название разрешение (пример Manifest.permission.CAMERA)
                    //  а значение boolean true/false (GRANTED, DENIED)
                    Set<Map.Entry<String, Boolean>> permissions = result.entrySet();
                    //  Коллекцию прогоняем через цикл
                    for (Map.Entry<String, Boolean> permission: permissions) {
                        //  Сопоставление ключа и константы Manifest.permission
                        switch (permission.getKey()) {
                            case Manifest.permission.CAMERA:
                                camera = permission.getValue();
                                break;
                            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                writeExternalStorage = permission.getValue();
                                break;
                            case Manifest.permission.READ_EXTERNAL_STORAGE:
                                readExternalStorage = permission.getValue();
                                break;
                        }
                    }
                    //  Все переменные должны быть true, чтобы соответствовать условию
                    if (camera && writeExternalStorage && readExternalStorage) {
                        Log.d(TAG, "Все разрешения предоставлены");
                        openCamera();
                    } else {
                        Log.d(TAG, "Не все разрешения предоставлены");
                        Toast.makeText(MainActivity.this, "Не все разрешения предоставлены", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
