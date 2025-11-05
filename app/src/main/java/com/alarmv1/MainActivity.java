package com.alarmv1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView alarmRecyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarms;
    private AlarmDatabase alarmDatabase;
    private TextView emptyView;
    private FloatingActionButton fabAddAlarm;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Alarms");
        }
        
        AlarmManagerHelper.initialize(this);
        alarmDatabase = new AlarmDatabase(this);
        
        alarmRecyclerView = findViewById(R.id.alarm_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        fabAddAlarm = findViewById(R.id.fab_add_alarm);
        
        alarms = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarms);
        alarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmRecyclerView.setAdapter(alarmAdapter);
        
        fabAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAlarmDialog(null);
            }
        });
        
        requestPermissions();
        loadAlarms();
    }
    
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        100);
            }
        }
    }
    
    private void loadAlarms() {
        alarms.clear();
        alarms.addAll(alarmDatabase.getAllAlarms());
        alarmAdapter.notifyDataSetChanged();
        updateEmptyView();
    }
    
    private void updateEmptyView() {
        if (alarms.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            alarmRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            alarmRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showAddAlarmDialog(final Alarm alarmToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_alarm, null);
        builder.setView(dialogView);
        
        final TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
        final com.google.android.material.textfield.TextInputEditText labelInput = 
                dialogView.findViewById(R.id.alarm_label_input);
        final Chip chipSun = dialogView.findViewById(R.id.chip_sun);
        final Chip chipMon = dialogView.findViewById(R.id.chip_mon);
        final Chip chipTue = dialogView.findViewById(R.id.chip_tue);
        final Chip chipWed = dialogView.findViewById(R.id.chip_wed);
        final Chip chipThu = dialogView.findViewById(R.id.chip_thu);
        final Chip chipFri = dialogView.findViewById(R.id.chip_fri);
        final Chip chipSat = dialogView.findViewById(R.id.chip_sat);
        final Button btnDelete = dialogView.findViewById(R.id.btn_delete);
        final Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        final Button btnSave = dialogView.findViewById(R.id.btn_save);
        final com.google.android.material.card.MaterialCardView ringtoneCard = 
                dialogView.findViewById(R.id.ringtone_card);
        final TextView selectedRingtoneName = dialogView.findViewById(R.id.selected_ringtone_name);
        
        Chip[] chips = {chipSun, chipMon, chipTue, chipWed, chipThu, chipFri, chipSat};
        
        // Initialize ringtone selection
        RingtoneManager ringtoneManager = RingtoneManager.getInstance(this);
        final int[] selectedRingtoneIndex = new int[1];
        
        if (alarmToEdit != null) {
            selectedRingtoneIndex[0] = alarmToEdit.getRingtoneIndex();
        } else {
            selectedRingtoneIndex[0] = 0; // Default to first ringtone
        }
        
        selectedRingtoneName.setText(ringtoneManager.getRingtoneName(selectedRingtoneIndex[0]));
        
        ringtoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRingtoneSelectionDialog(selectedRingtoneIndex, selectedRingtoneName, ringtoneManager);
            }
        });
        
        if (alarmToEdit != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(alarmToEdit.getHour());
                timePicker.setMinute(alarmToEdit.getMinute());
            } else {
                timePicker.setCurrentHour(alarmToEdit.getHour());
                timePicker.setCurrentMinute(alarmToEdit.getMinute());
            }
            labelInput.setText(alarmToEdit.getLabel());
            boolean[] repeatDays = alarmToEdit.getRepeatDays();
            for (int i = 0; i < 7; i++) {
                chips[i].setChecked(repeatDays[i]);
            }
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY));
                timePicker.setMinute(java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE));
            }
        }
        
        final AlertDialog dialog = builder.create();
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmToEdit != null) {
                    deleteAlarm(alarmToEdit);
                    dialog.dismiss();
                }
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                
                String label = labelInput.getText().toString();
                boolean[] repeatDays = new boolean[7];
                for (int i = 0; i < 7; i++) {
                    repeatDays[i] = chips[i].isChecked();
                }
                
                Alarm alarm;
                if (alarmToEdit != null) {
                    alarm = alarmToEdit;
                    alarm.setHour(hour);
                    alarm.setMinute(minute);
                    alarm.setLabel(label);
                    alarm.setRepeatDays(repeatDays);
                    alarm.setRingtoneIndex(selectedRingtoneIndex[0]);
                    updateAlarm(alarm);
                } else {
                    alarm = new Alarm();
                    alarm.setHour(hour);
                    alarm.setMinute(minute);
                    alarm.setLabel(label);
                    alarm.setRepeatDays(repeatDays);
                    alarm.setRingtoneIndex(selectedRingtoneIndex[0]);
                    addAlarm(alarm);
                }
                
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    private void addAlarm(Alarm alarm) {
        alarmDatabase.addAlarm(alarm);
        AlarmManagerHelper.setAlarm(alarm);
        loadAlarms();
        Toast.makeText(this, "Alarm added", Toast.LENGTH_SHORT).show();
    }
    
    private void updateAlarm(Alarm alarm) {
        AlarmManagerHelper.cancelAlarm(alarm);
        alarmDatabase.updateAlarm(alarm);
        if (alarm.isEnabled()) {
            AlarmManagerHelper.setAlarm(alarm);
        }
        loadAlarms();
        Toast.makeText(this, "Alarm updated", Toast.LENGTH_SHORT).show();
    }
    
    private void deleteAlarm(Alarm alarm) {
        AlarmManagerHelper.cancelAlarm(alarm);
        alarmDatabase.deleteAlarm(alarm.getId());
        loadAlarms();
        Toast.makeText(this, "Alarm deleted", Toast.LENGTH_SHORT).show();
    }
    
    private class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
        private List<Alarm> alarmList;
        
        public AlarmAdapter(List<Alarm> alarmList) {
            this.alarmList = alarmList;
        }
        
        @NonNull
        @Override
        public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_alarm, parent, false);
            return new AlarmViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
            Alarm alarm = alarmList.get(position);
            holder.bind(alarm);
        }
        
        @Override
        public int getItemCount() {
            return alarmList.size();
        }
        
        class AlarmViewHolder extends RecyclerView.ViewHolder {
            private TextView alarmTime;
            private TextView alarmLabel;
            private TextView alarmRepeat;
            private SwitchMaterial alarmSwitch;
            
            public AlarmViewHolder(@NonNull View itemView) {
                super(itemView);
                alarmTime = itemView.findViewById(R.id.alarm_time);
                alarmLabel = itemView.findViewById(R.id.alarm_label);
                alarmRepeat = itemView.findViewById(R.id.alarm_repeat);
                alarmSwitch = itemView.findViewById(R.id.alarm_switch);
            }
            
            public void bind(final Alarm alarm) {
                alarmTime.setText(alarm.getTimeString());
                
                if (alarm.getLabel() != null && !alarm.getLabel().isEmpty()) {
                    alarmLabel.setText(alarm.getLabel());
                    alarmLabel.setVisibility(View.VISIBLE);
                } else {
                    alarmLabel.setVisibility(View.GONE);
                }
                
                String repeatText = alarm.getRepeatText();
                if (!repeatText.isEmpty()) {
                    alarmRepeat.setText(repeatText);
                    alarmRepeat.setVisibility(View.VISIBLE);
                } else {
                    alarmRepeat.setText("Once");
                    alarmRepeat.setVisibility(View.VISIBLE);
                }
                
                alarmSwitch.setChecked(alarm.isEnabled());
                
                alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        alarm.setEnabled(isChecked);
                        if (isChecked) {
                            AlarmManagerHelper.setAlarm(alarm);
                        } else {
                            AlarmManagerHelper.cancelAlarm(alarm);
                        }
                        alarmDatabase.updateAlarm(alarm);
                    }
                });
                
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddAlarmDialog(alarm);
                    }
                });
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    
    private void showRingtoneSelectionDialog(final int[] selectedIndex, final TextView selectedNameView, 
                                             final RingtoneManager ringtoneManager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ringtone_selection, null);
        builder.setView(dialogView);
        
        RecyclerView recyclerView = dialogView.findViewById(R.id.ringtone_recycler_view);
        List<RingtoneManager.RingtoneInfo> ringtones = RingtoneManager.getAvailableRingtones(this);
        RingtoneAdapter adapter = new RingtoneAdapter(ringtones, selectedIndex, ringtoneManager, selectedNameView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        final AlertDialog dialog = builder.create();
        adapter.setDialog(dialog);
        adapter.setSelectedIndexArray(selectedIndex);
        dialog.show();
    }
    
    private class RingtoneAdapter extends RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder> {
        private List<RingtoneManager.RingtoneInfo> ringtones;
        private int selectedIndex;
        private RingtoneManager ringtoneManager;
        private TextView selectedNameView;
        private AlertDialog dialog;
        private int[] selectedIndexArray;
        
        public RingtoneAdapter(List<RingtoneManager.RingtoneInfo> ringtones, int[] selectedIndexArray, 
                               RingtoneManager ringtoneManager, TextView selectedNameView) {
            this.ringtones = ringtones;
            this.selectedIndexArray = selectedIndexArray;
            this.selectedIndex = selectedIndexArray[0];
            this.ringtoneManager = ringtoneManager;
            this.selectedNameView = selectedNameView;
        }
        
        public void setDialog(AlertDialog dialog) {
            this.dialog = dialog;
        }
        
        public void setSelectedIndexArray(int[] selectedIndexArray) {
            this.selectedIndexArray = selectedIndexArray;
            this.selectedIndex = selectedIndexArray[0];
        }
        
        @NonNull
        @Override
        public RingtoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ringtone, parent, false);
            return new RingtoneViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull RingtoneViewHolder holder, int position) {
            RingtoneManager.RingtoneInfo ringtone = ringtones.get(position);
            holder.bind(ringtone, position == selectedIndex);
        }
        
        @Override
        public int getItemCount() {
            return ringtones.size();
        }
        
        class RingtoneViewHolder extends RecyclerView.ViewHolder {
            private TextView ringtoneName;
            private Button previewButton;
            private android.widget.ImageView checkIcon;
            
            public RingtoneViewHolder(@NonNull View itemView) {
                super(itemView);
                ringtoneName = itemView.findViewById(R.id.ringtone_name);
                previewButton = itemView.findViewById(R.id.btn_preview);
                checkIcon = itemView.findViewById(R.id.check_icon);
            }
            
            public void bind(final RingtoneManager.RingtoneInfo ringtone, boolean isSelected) {
                ringtoneName.setText(ringtone.getName());
                checkIcon.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                
                previewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ringtoneManager.previewRingtone(ringtone.getIndex());
                    }
                });
                
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int previousIndex = RingtoneAdapter.this.selectedIndex;
                        RingtoneAdapter.this.selectedIndex = ringtone.getIndex();
                        if (RingtoneAdapter.this.selectedIndexArray != null) {
                            RingtoneAdapter.this.selectedIndexArray[0] = ringtone.getIndex();
                        }
                        selectedNameView.setText(ringtone.getName());
                        ringtoneManager.stopPreview();
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        notifyItemChanged(previousIndex);
                        notifyItemChanged(RingtoneAdapter.this.selectedIndex);
                    }
                });
            }
        }
    }
}

