package com.juegos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private final GestorDocumentos gestor;

    // Campos
    private final JTextField tfId = new JTextField();
    private final JTextField tfTitulo = new JTextField();
    private final JTextField tfDesarrollador = new JTextField();
    private final JTextField tfAnio = new JTextField();
    private final JTextField tfGeneros = new JTextField();
    private final JTextField tfPlataformas = new JTextField();
    private final JTextField tfPrecio = new JTextField();
    private final JCheckBox cbDisponible = new JCheckBox("Disponible");
    private final JTextArea taDescripcion = new JTextArea(3, 20);

    // Búsqueda
    private final JComboBox<String> cbBuscarCampo = new JComboBox<>(new String[]{"titulo", "desarrollador", "descripcion"});
    private final JTextField tfBuscar = new JTextField();

    // Tabla
    private final DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"ID", "Título", "Desarrollador", "Año", "Géneros", "Plataformas", "Precio", "¿Disponible?"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    // Botones
    private JButton btnModificar;
    private JButton btnEliminar;
    private final JLabel lblEstado = new JLabel("Listo");

    public MainFrame() {
        super("Gestión de Videojuegos");
        this.gestor = new GestorDocumentos();
        initUI();
        cargarTabla();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // --- Formulario (Norte) ---
        JPanel panelForm = new JPanel(new GridLayout(0, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del videojuego"));

        tfId.setEditable(false);
        panelForm.add(new JLabel("ID:")); panelForm.add(tfId);
        panelForm.add(new JLabel("Título:")); panelForm.add(tfTitulo);
        panelForm.add(new JLabel("Desarrollador:")); panelForm.add(tfDesarrollador);
        panelForm.add(new JLabel("Año:")); panelForm.add(tfAnio);
        panelForm.add(new JLabel("Géneros (coma):")); panelForm.add(tfGeneros);
        panelForm.add(new JLabel("Plataformas (coma):")); panelForm.add(tfPlataformas);
        panelForm.add(new JLabel("Precio (€):")); panelForm.add(tfPrecio);
        panelForm.add(new JLabel("")); panelForm.add(cbDisponible);
        panelForm.add(new JLabel("Descripción:"));
        taDescripcion.setLineWrap(true);
        panelForm.add(new JScrollPane(taDescripcion));

        // --- Búsqueda (Norte, al lado o arriba) ---
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBuscar.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        panelBuscar.add(new JLabel("Buscar por:"));
        panelBuscar.add(cbBuscarCampo);
        panelBuscar.add(tfBuscar);
        tfBuscar.setPreferredSize(new Dimension(150, 25));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiarBusqueda = new JButton("Limpiar búsqueda");
        panelBuscar.add(btnBuscar);
        panelBuscar.add(btnLimpiarBusqueda);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelForm, BorderLayout.CENTER);
        panelNorte.add(panelBuscar, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // --- Tabla (Centro) ---
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccion();
        });

        // --- Botones (Sur) ---
        JPanel panelSur = new JPanel(new BorderLayout());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAnadir = new JButton("Añadir");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar campos");

        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnAnadir);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelEstado.add(lblEstado);

        panelSur.add(panelBotones, BorderLayout.WEST);
        panelSur.add(panelEstado, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);

        // Listeners
        btnAnadir.addActionListener(e -> anadir());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscar.addActionListener(e -> buscar());
        btnLimpiarBusqueda.addActionListener(e -> { tfBuscar.setText(""); cargarTabla(); });
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (Videojuego v : gestor.obtenerTodos()) {
            modelo.addRow(new Object[]{
                v.getId(),
                v.getTitulo(),
                v.getDesarrollador(),
                v.getAnio(),
                listaAString(v.getGeneros()),
                listaAString(v.getPlataformas()),
                v.getPrecio(),
                v.isDisponible() ? "Sí" : "No"
            });
        }
        lblEstado.setText("Registros: " + modelo.getRowCount());
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        tfId.setText(modelo.getValueAt(fila, 0).toString());
        tfTitulo.setText(modelo.getValueAt(fila, 1).toString());
        tfDesarrollador.setText(modelo.getValueAt(fila, 2).toString());
        tfAnio.setText(modelo.getValueAt(fila, 3).toString());
        tfGeneros.setText(modelo.getValueAt(fila, 4).toString());
        tfPlataformas.setText(modelo.getValueAt(fila, 5).toString());
        tfPrecio.setText(modelo.getValueAt(fila, 6).toString());
        cbDisponible.setSelected("Sí".equals(modelo.getValueAt(fila, 7)));

        // Cargar descripción completa desde el gestor
        int id = Integer.parseInt(tfId.getText());
        Videojuego v = gestor.obtenerPorId(id);
        taDescripcion.setText(v != null ? v.getDescripcion() : "");

        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private Videojuego leerCampos() {
        Videojuego v = new Videojuego();
        if (!tfId.getText().isEmpty()) v.setId(Integer.parseInt(tfId.getText()));
        v.setTitulo(tfTitulo.getText().trim());
        v.setDesarrollador(tfDesarrollador.getText().trim());
        try {
            v.setAnio(Integer.parseInt(tfAnio.getText().trim()));
        } catch (NumberFormatException e) {
            throw new RuntimeException("El año debe ser un número");
        }
        v.setGeneros(splitComa(tfGeneros.getText()));
        v.setPlataformas(splitComa(tfPlataformas.getText()));
        try {
            v.setPrecio(Double.parseDouble(tfPrecio.getText().trim().replace(",", ".")));
        } catch (NumberFormatException e) {
            v.setPrecio(0.0);
        }
        v.setDisponible(cbDisponible.isSelected());
        v.setDescripcion(taDescripcion.getText());
        return v;
    }

    private List<String> splitComa(String texto) {
        if (texto == null || texto.isEmpty()) return List.of();
        return Arrays.stream(texto.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String listaAString(List<String> lista) {
        return lista == null ? "" : String.join(", ", lista);
    }

    private void anadir() {
        try {
            Videojuego v = leerCampos();
            gestor.anadirDocumento(v);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Videojuego añadido correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificar() {
        try {
            if (tfId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona un registro de la tabla", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(tfId.getText());
            Videojuego v = leerCampos();
            gestor.modificarDocumento(id, v);
            cargarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Videojuego modificado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        try {
            if (tfId.getText().isEmpty()) return;
            int id = Integer.parseInt(tfId.getText());
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar el juego con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            gestor.eliminarDocumento(id);
            cargarTabla();
            limpiarCampos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String campo = (String) cbBuscarCampo.getSelectedItem();
        String valor = tfBuscar.getText().trim();
        if (valor.isEmpty()) {
            cargarTabla();
            return;
        }
        List<Videojuego> resultados = gestor.buscarPorCampo(campo, valor);
        modelo.setRowCount(0);
        for (Videojuego v : resultados) {
            modelo.addRow(new Object[]{
                v.getId(), v.getTitulo(), v.getDesarrollador(), v.getAnio(),
                listaAString(v.getGeneros()), listaAString(v.getPlataformas()),
                v.getPrecio(), v.isDisponible() ? "Sí" : "No"
            });
        }
        lblEstado.setText("Resultados: " + resultados.size());
    }

    private void limpiarCampos() {
        tfId.setText("");
        tfTitulo.setText("");
        tfDesarrollador.setText("");
        tfAnio.setText("");
        tfGeneros.setText("");
        tfPlataformas.setText("");
        tfPrecio.setText("");
        cbDisponible.setSelected(false);
        taDescripcion.setText("");
        tabla.clearSelection();
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
}