package com.juegos;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private final GestorDocumentos gestor;

    // Campos de entrada
    private final JTextField tfId = new JTextField(8);
    private final JTextField tfTitle = new JTextField(20);
    private final JTextField tfDeveloper = new JTextField(20);
    private final JTextField tfYear = new JTextField(6);
    private final JTextField tfGenres = new JTextField(20);
    private final JTextField tfPlatforms = new JTextField(20);
    private final JCheckBox cbMultiplayer = new JCheckBox("Multiplayer");
    private final JTextField tfPrice = new JTextField(8);
    private final JCheckBox cbAvailable = new JCheckBox("Disponible");
    private final JTextArea taDescription = new JTextArea(3, 40);

    // Search controls
    private final JComboBox<String> cbSearchField = new JComboBox<>(new String[]{"title", "developer", "genres", "platforms", "tags", "description"});
    private final JTextField tfSearchValue = new JTextField(15);

    // Tabla
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Título", "Developer", "Año", "Géneros", "Plataformas", "Precio", "Disponible"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    // Botones que deben poder habilitarse/deshabilitarse
    private JButton btnModify;
    private JButton btnDelete;

    // Estado
    private final JLabel statusLabel = new JLabel("Listo");

    public MainFrame() {
        super("Gestión de Videojuegos");
        this.gestor = new GestorDocumentos();

        initUI();
        loadTableData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));

        // Panel superior: campos de entrada y búsqueda
        JPanel topPanel = new JPanel(new BorderLayout(6, 6));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; fieldsPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; fieldsPanel.add(tfId, gbc); tfId.setEditable(false);

        gbc.gridx = 2; fieldsPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 3; fieldsPanel.add(tfTitle, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; fieldsPanel.add(new JLabel("Developer:"), gbc);
        gbc.gridx = 1; fieldsPanel.add(tfDeveloper, gbc);

        gbc.gridx = 2; fieldsPanel.add(new JLabel("Año:"), gbc);
        gbc.gridx = 3; fieldsPanel.add(tfYear, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; fieldsPanel.add(new JLabel("Géneros (coma):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; fieldsPanel.add(tfGenres, gbc); gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row; fieldsPanel.add(new JLabel("Plataformas (coma):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; fieldsPanel.add(tfPlatforms, gbc); gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row; fieldsPanel.add(cbMultiplayer, gbc);
        gbc.gridx = 1; fieldsPanel.add(new JLabel("Precio (€):"), gbc);
        gbc.gridx = 2; fieldsPanel.add(tfPrice, gbc);
        gbc.gridx = 3; fieldsPanel.add(cbAvailable, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4; fieldsPanel.add(new JLabel("Descripción:"), gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4; fieldsPanel.add(new JScrollPane(taDescription), gbc); gbc.gridwidth = 1;

        topPanel.add(fieldsPanel, BorderLayout.CENTER);

        // Búsqueda arriba a la derecha
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Buscar por:"));
        searchPanel.add(cbSearchField);
        searchPanel.add(tfSearchValue);
        JButton btnSearchTop = new JButton("Buscar");
        searchPanel.add(btnSearchTop);
        btnSearchTop.addActionListener(e -> onBuscar(true));

        JButton btnClearSearch = new JButton("Limpiar búsqueda");
        searchPanel.add(btnClearSearch);
        btnClearSearch.addActionListener(e -> {
            tfSearchValue.setText("");
            loadTableData();
        });

        // Búsqueda en tiempo real: DocumentListener (opcional avanzado)
        tfSearchValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { realTimeSearch(); }

            @Override
            public void removeUpdate(DocumentEvent e) { realTimeSearch(); }

            @Override
            public void changedUpdate(DocumentEvent e) { realTimeSearch(); }

            private void realTimeSearch() {
                String text = tfSearchValue.getText();
                if (text == null || text.trim().isEmpty()) {
                    loadTableData();
                } else {
                    onBuscar(false);
                }
            }
        });

        topPanel.add(searchPanel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        // Panel central: tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Selección de fila carga en campos; habilita botones de modificar/eliminar
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    onTableSelectionChanged();
                    int sel = table.getSelectedRow();
                    boolean has = sel >= 0;
                    btnModify.setEnabled(has);
                    btnDelete.setEnabled(has);
                }
            }
        });

        // Panel inferior: botones
            JPanel bottomPanel = new JPanel(new BorderLayout());
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

            // botones como campos para poder habilitarlos/deshabilitarlos desde otros métodos
            JButton btnAdd = new JButton("Añadir");
            this.btnModify = new JButton("Modificar");
            this.btnDelete = new JButton("Eliminar");
            JButton btnSearch = new JButton("Buscar");
            JButton btnClear = new JButton("Limpiar");

            // Inicialmente no hay selección
            this.btnModify.setEnabled(false);
            this.btnDelete.setEnabled(false);

            buttons.add(btnAdd);
            buttons.add(this.btnModify);
            buttons.add(this.btnDelete);
            buttons.add(btnSearch);
            buttons.add(btnClear);

            bottomPanel.add(buttons, BorderLayout.WEST);

        // Status label
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.add(statusLabel);
        bottomPanel.add(statusPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> onAñadir());
        btnModify.addActionListener(e -> onModificar());
        btnDelete.addActionListener(e -> onEliminar());
        btnSearch.addActionListener(e -> onBuscar());
        btnClear.addActionListener(e -> onLimpiar());
    }

    private void loadTableData() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            List<Videojuego> lista = gestor.obtenerTodos();
            DecimalFormat df = new DecimalFormat("0.00");
            for (Videojuego v : lista) {
                String genres = v.getGenres() == null ? "" : String.join(", ", v.getGenres());
                String plats = v.getPlatforms() == null ? "" : String.join(", ", v.getPlatforms());
                String price = df.format(v.getPriceCents() / 100.0);
                tableModel.addRow(new Object[]{v.getId(), v.getTitle(), v.getDeveloper(), v.getReleaseYear(), genres, plats, price, v.isAvailable()});
            }
            statusLabel.setText("Cargados: " + lista.size());
        });
    }

    private void onTableSelectionChanged() {
        try {
            int row = table.getSelectedRow();
            if (row < 0) return;
            Object idVal = tableModel.getValueAt(row, 0);
            if (idVal == null) return;
            tfId.setText(String.valueOf(idVal));
            tfTitle.setText(String.valueOf(tableModel.getValueAt(row, 1)));
            tfDeveloper.setText(String.valueOf(tableModel.getValueAt(row, 2)));
            tfYear.setText(String.valueOf(tableModel.getValueAt(row, 3)));
            tfGenres.setText(String.valueOf(tableModel.getValueAt(row, 4)));
            tfPlatforms.setText(String.valueOf(tableModel.getValueAt(row, 5)));
            tfPrice.setText(String.valueOf(tableModel.getValueAt(row, 6)));
            cbAvailable.setSelected(Boolean.TRUE.equals(tableModel.getValueAt(row, 7)));
            // cargar descripción desde gestor (más completo)
            int id = Integer.parseInt(tfId.getText());
            Videojuego v = gestor.obtenerPorId(id);
            if (v != null) taDescription.setText(v.getDescription());
        } catch (NullPointerException | NumberFormatException ex) {
            ex.printStackTrace();
            showError("Error al cargar selección: " + ex.getMessage());
        }
    }

    private List<String> splitCSV(String text) {
        if (text == null) return new ArrayList<>();
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // Validaciones adicionales para evitar caracteres peligrosos
    private void validateSafeString(String fieldName, String value, boolean mandatory) {
        if (mandatory && (value == null || value.trim().isEmpty())) {
            throw new IllegalArgumentException(fieldName + " es obligatorio");
        }
        if (value != null) {
            // Rechazar etiquetas HTML simples para evitar inyección
            if (value.contains("<") || value.contains(">")) {
                throw new IllegalArgumentException(fieldName + " contiene caracteres no permitidos ('<' o '>')");
            }
            // Rechazar caracteres de control
            for (char c : value.toCharArray()) {
                if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                    throw new IllegalArgumentException(fieldName + " contiene caracteres inválidos");
                }
            }
        }
    }

    private void onAñadir() {
        try {
            Videojuego v = buildFromFields(false);
            Videojuego creado = gestor.añadirDocumento(v);
            statusLabel.setText("Añadido id=" + creado.getId());
            loadTableData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Videojuego añadido (id=" + creado.getId() + ")", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            showError(iae.getMessage());
        } catch (GestorDocumentos.GestorException ge) {
            ge.printStackTrace();
            showError(ge.getMessage());
        } catch (JsonDatabase.JsonDatabaseException je) {
            je.printStackTrace();
            showError("Error de persistencia: " + je.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error inesperado: " + ex.getMessage());
        }
    }

    private void onModificar() {
        try {
            if (tfId.getText().trim().isEmpty()) { showError("Seleccione un registro para modificar"); return; }
            int id;
            try {
                id = Integer.parseInt(tfId.getText().trim());
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("ID inválido");
            }
            Videojuego v = buildFromFields(true);
            gestor.modificarDocumento(id, v);
            statusLabel.setText("Modificado id=" + id);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Videojuego modificado (id=" + id + ")", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            showError(iae.getMessage());
        } catch (GestorDocumentos.GestorException ge) {
            ge.printStackTrace();
            showError(ge.getMessage());
        } catch (JsonDatabase.JsonDatabaseException je) {
            je.printStackTrace();
            showError("Error de persistencia: " + je.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error inesperado: " + ex.getMessage());
        }
    }

    private void onEliminar() {
        try {
            if (tfId.getText().trim().isEmpty()) { showError("Seleccione un registro para eliminar"); return; }
            int id;
            try {
                id = Integer.parseInt(tfId.getText().trim());
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("ID inválido");
            }
            int ans = JOptionPane.showConfirmDialog(this, "¿Eliminar el videojuego id=" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) return;
            gestor.eliminarDocumento(id);
            statusLabel.setText("Eliminado id=" + id);
            loadTableData();
            clearFields();
            JOptionPane.showMessageDialog(this, "Videojuego eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            showError(iae.getMessage());
        } catch (GestorDocumentos.GestorException ge) {
            ge.printStackTrace();
            showError(ge.getMessage());
        } catch (JsonDatabase.JsonDatabaseException je) {
            je.printStackTrace();
            showError("Error de persistencia: " + je.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error inesperado: " + ex.getMessage());
        }
    }

    private void onBuscar() {
        onBuscar(true);
    }

    /**
     * Ejecuta búsqueda usando GestorDocumentos. Si showEmptyError==false no mostrará
     * un error cuando el término de búsqueda esté vacío (útil para búsquedas en tiempo real).
     */
    private void onBuscar(boolean showEmptyError) {
        try {
            String campo = (String) cbSearchField.getSelectedItem();
            String valor = tfSearchValue.getText();
            if (valor == null || valor.trim().isEmpty()) {
                if (showEmptyError) showError("Introduzca valor de búsqueda");
                else loadTableData();
                return;
            }
            List<Videojuego> resultados = gestor.buscarPorCampo(campo, valor.trim());
            // Mostrar resultados en tabla
            tableModel.setRowCount(0);
            DecimalFormat df = new DecimalFormat("0.00");
            for (Videojuego v : resultados) {
                String genres = v.getGenres() == null ? "" : String.join(", ", v.getGenres());
                String plats = v.getPlatforms() == null ? "" : String.join(", ", v.getPlatforms());
                String price = df.format(v.getPriceCents() / 100.0);
                tableModel.addRow(new Object[]{v.getId(), v.getTitle(), v.getDeveloper(), v.getReleaseYear(), genres, plats, price, v.isAvailable()});
            }
            if (resultados.isEmpty()) {
                statusLabel.setText("No se encontraron documentos");
                JOptionPane.showMessageDialog(this, "No se encontraron documentos", "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("Resultados: " + resultados.size());
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onLimpiar() {
        clearFields();
        loadTableData();
    }

    private Videojuego buildFromFields(boolean forUpdate) {
        Videojuego v = new Videojuego();
        if (forUpdate) {
            String idText = tfId.getText().trim();
            if (!idText.isEmpty()) v.setId(Integer.parseInt(idText));
        }
        // Validaciones de seguridad y obligatoriedad
        validateSafeString("Título", tfTitle.getText(), true);
        validateSafeString("Developer", tfDeveloper.getText(), true);
        validateSafeString("Descripción", taDescription.getText(), false);

        v.setTitle(tfTitle.getText());
        v.setDeveloper(tfDeveloper.getText());
        try {
            v.setReleaseYear(Integer.parseInt(tfYear.getText().trim()));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Año inválido");
        }
        v.setGenres(splitCSV(tfGenres.getText()));
        v.setPlatforms(splitCSV(tfPlatforms.getText()));
        v.setMultiplayer(cbMultiplayer.isSelected());
        // price en euros => cents
        try {
            double euros = tfPrice.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(tfPrice.getText().trim());
            v.setPriceCents((int) Math.round(euros * 100));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Precio inválido");
        }
        v.setAvailable(cbAvailable.isSelected());
        v.setDescription(taDescription.getText());
        // rating y metadata mínimos
        Videojuego.Rating rating = new Videojuego.Rating();
        rating.setScore(0.0);
        rating.setVotes(0);
        v.setRating(rating);
        Videojuego.Metadata md = new Videojuego.Metadata();
        v.setMetadata(md);
        return v;
    }

    private void clearFields() {
        tfId.setText("");
        tfTitle.setText("");
        tfDeveloper.setText("");
        tfYear.setText("");
        tfGenres.setText("");
        tfPlatforms.setText("");
        cbMultiplayer.setSelected(false);
        tfPrice.setText("");
        cbAvailable.setSelected(false);
        taDescription.setText("");
        tfSearchValue.setText("");
        table.clearSelection();
    }

    private void showError(String msg) {
        statusLabel.setText("Error: " + msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
