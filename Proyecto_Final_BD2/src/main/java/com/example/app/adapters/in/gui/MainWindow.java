package com.example.app.adapters.in.gui;

import com.example.app.application.service.ClienteService;
import com.example.app.domain.model.Cliente;
import com.example.app.infrastructure.jdbc.ClienteRepositoryJdbc;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MainWindow extends JFrame {
    private final ClienteService clienteService;

    private final JTextField txtId = new JTextField(8);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDocumento = new JTextField(12);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(12);

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> clienteList = new JList<>(listModel);

    public MainWindow() {
        clienteService = new ClienteService(new ClienteRepositoryJdbc());
        setTitle("Aplicación Clientes - Hexagonal (CRUDL)");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));
        setLocationRelativeTo(null);

        // Panel formulario
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("ID:"), c);
        c.gridx = 1; form.add(txtId, c);
        c.gridx = 2; form.add(new JLabel("Documento:"), c);
        c.gridx = 3; form.add(txtDocumento, c);

        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Nombre:"), c);
        c.gridx = 1; c.gridwidth = 3; form.add(txtNombre, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Correo:"), c);
        c.gridx = 1; form.add(txtCorreo, c);
        c.gridx = 2; form.add(new JLabel("Teléfono:"), c);
        c.gridx = 3; form.add(txtTelefono, c);

        add(form, BorderLayout.NORTH);

        // Panel botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar por ID");
        JButton btnListar = new JButton("Listar Todos");

        botones.add(btnCrear); botones.add(btnActualizar); botones.add(btnEliminar);
        botones.add(btnBuscar); botones.add(btnListar);
        add(botones, BorderLayout.CENTER);

        // Panel lista
        add(new JScrollPane(clienteList), BorderLayout.SOUTH);

        // Listeners
        btnCrear.addActionListener(e -> crearCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnBuscar.addActionListener(e -> buscarPorId());
        btnListar.addActionListener(e -> listar());

        // Doble clic en la lista para cargar en formulario
        clienteList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String sel = clienteList.getSelectedValue();
                    if (sel != null && sel.contains(" - ")) {
                        String idStr = sel.split(" - ")[0].trim();
                        try {
                            Integer id = Integer.parseInt(idStr);
                            clienteService.obtenerPorId(id).ifPresent(MainWindow.this::cargarEnFormulario);
                        } catch (NumberFormatException ex) { /* ignore */ }
                    }
                }
            }
        });

        // Carga inicial
        listar();
    }

    private void crearCliente() {
        try {
            Cliente c = new Cliente();
            c.setNombre(txtNombre.getText().trim());
            c.setDocumento(txtDocumento.getText().trim().isEmpty() ? null : txtDocumento.getText().trim());
            c.setCorreo(txtCorreo.getText().trim());
            c.setTelefono(txtTelefono.getText().trim());
            Cliente creado = clienteService.crearCliente(c);
            JOptionPane.showMessageDialog(this, "Cliente creado con ID: " + creado.getId());
            limpiarCampos();
            listar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCliente() {
        try {
            Integer id = Integer.valueOf(txtId.getText().trim());
            Cliente c = new Cliente(id, txtNombre.getText().trim(), txtDocumento.getText().trim(), txtCorreo.getText().trim(), txtTelefono.getText().trim());
            clienteService.actualizarCliente(c);
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
            limpiarCampos();
            listar();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "ID inválido para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        try {
            Integer id = Integer.valueOf(txtId.getText().trim());
            int r = JOptionPane.showConfirmDialog(this, "Eliminar cliente ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                clienteService.eliminarCliente(id);
                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                limpiarCampos();
                listar();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "ID inválido para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorId() {
        try {
            Integer id = Integer.valueOf(txtId.getText().trim());
            Optional<Cliente> oc = clienteService.obtenerPorId(id);
            if (oc.isPresent()) {
                cargarEnFormulario(oc.get());
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listar() {
        try {
            listModel.clear();
            List<Cliente> clientes = clienteService.listarClientes();
            for (Cliente c : clientes) {
                listModel.addElement(String.format("%d - %s - %s - %s", c.getId(), c.getNombre(), c.getDocumento()==null?"":c.getDocumento(), c.getCorreo()));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEnFormulario(Cliente c) {
        txtId.setText(c.getId() == null ? "" : c.getId().toString());
        txtNombre.setText(c.getNombre());
        txtDocumento.setText(c.getDocumento());
        txtCorreo.setText(c.getCorreo());
        txtTelefono.setText(c.getTelefono());
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDocumento.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow w = new MainWindow();
            w.setVisible(true);
        });
    }
}
