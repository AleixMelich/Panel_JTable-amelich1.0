package org.amelich;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


/**
 * @author Aleix Melich
 * @version 1.0
 */
public class Alumne extends JFrame{
    private JPanel panel;
    private JTable taula;
    private JButton modificarButton;
    private JTextField campEdat;
    private JButton borrarButton;
    private JButton insertarButton;
    private JTextField campNom;
    private JTextField campNota;
    private JButton guardarButton;
    private JCheckBox SI_CheckBox;
    private JButton exportarButton;

    public static final String FITXER="./src/main/resources/dades.dat";    // DEFINIM EL NOM DEL FITXER ON GUARDAREM LES DADES
    File f = new File(FITXER);
    public boolean botoGuardarPresionat = false; // PER A SABER SI S'HA PRESSIONAT EL BOTO DE GUARDAR




    /**
     * @return true si algun dels camps està buit, false en cas contrari.
     */
    public boolean algunCampBuit() {
        return campNom.getText().isBlank() || campEdat.getText().isBlank() || campNota.getText().isBlank();
    }

    /**
     * Aquest mètode s'encarrega de parsejar una cadena a un doble i verificar que estigui dins del rang de 0 a 10.
     * @param entrada La cadena que s'analitzarà.
     * @return El valor doble analitzat.
     * @throws ParseException Si el doble analitzat no està dins del rang de 0 a 10.
     */
    public double parsearIVerificarNota(String entrada) throws ParseException {
        NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
        double nota = num.parse(entrada.trim()).doubleValue();
        if (nota < 0 || nota > 10) {
            throw new ParseException("El doble analitzat no es un numero entre 0 i 10.", 0);
        }
        return nota;
    }

    /**
     * @return Deixa els camps en blanc.
     */
    private void llimpiarCamps() {
        campNom.setText("");
        campEdat.setText("");
        campNota.setText("");
        SI_CheckBox.setSelected(false);
        campNom.requestFocus();
    }

    /**
     * Constructor de la classe Alumne
     * Aquest constructor s'encarrega de configurar la finestra i els elements que la composen.
     * A més a més, s'encarrega de gestionar els events que es produeixen en els elements de la finestra.
     * També s'encarrega de llegir les dades del fitxer i de guardar-les quan es tanca la finestra.
     * @see AlumnePojo
     */
    public Alumne(){
        // CONFIGURACIONS DE LA FINESTRA
        this.setContentPane(panel); // DEFINIM EL PANEL QUE CONTÉ TOTS ELS ELEMENTS
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // PERMETRE TANCAR LA FINESTRA
        this.pack(); // AJUSTAR LA FINESTRA AL CONTINGUT
        this.setVisible(true); // FER VISIBLE LA FINESTRA
        this.setSize(800, 600); // DEFINIM LA MIDA DE LA FINESTRA
        this.setLocationRelativeTo(null); // CENTRAR LA FINESTRA
        this.setTitle("Gestió d'alumnes"); // DEFINIM EL TITOL DE LA FINESTRA
        this.setIconImage(new ImageIcon("./src/main/resources/imagen.jpg").getImage()); // DEFINIM LA ICONA DE LA FINESTRA
        Locale.setDefault(new Locale("ca","ES")); // DEFINIM LA CULTURA DE LA NOSTRA APLICACIÓ


        // CONFIGURACIONS DE LA TAULA
        DefaultTableModel model=new DefaultTableModel(new Object[]{"Nom i cognom","Edat", "Nota", "FCT"},0) {
            // PERMET DEFINIR EL TIPUS DE CADA COLUMNA
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Integer.class;
                    case 2:
                        return Double.class;
                    case 3:
                        return Boolean.class;
                    default:
                        return Object.class;
                }
            }
            // PERMET DETERMINAR QUE LES CEL·LES NO SON EDITABLES
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taula.setModel(model);
        taula.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD,13)); // CANVIEM LA FONT DE LA TAULA
        taula.setRowHeight(23); // DEFINIM L'ALÇADA DE LES FILES
        taula.getTableHeader().setBackground(new Color(32, 136, 203));  // CANVIEM EL COLOR DE FONS DE LA TAULA
        taula.getTableHeader().setForeground(new Color(255, 255, 255)); // CANVIEM EL COLOR DE LA LLETRA DE LA TAULA
        taula.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  //SELECCIONAR NOMES UNA FILA
        taula.getTableHeader().setReorderingAllowed(false); //NO PERMETRE MOURE LES COLUMNES
        taula.getTableHeader().setResizingAllowed(false); //NO PERMETRE REDIMENSIONAR LES COLUMNES
        taula.setDefaultEditor(Object.class, null); // NO PERMETRE EDITAR LES CASSELLES DE LA TAULA
        taula.setCursor(new Cursor(Cursor.HAND_CURSOR)); // CANVIEM EL CURSOR DE LA TAULA
        campNom.requestFocus();     // FEM QUE EL FOCO VAIGUE AL CAMP DEL NOM


        // CODI PER A QUE AL TANCAR LA FINESTRA ENS DEMANI SI VOLEM GUARDAR LES DADES
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (model.getRowCount() > 0 && !botoGuardarPresionat){
                    int confirm = JOptionPane.showConfirmDialog(
                            null, "Vols guardar les dades abans de tancar?",
                            "Confirmació de sortida", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        guardarButton.doClick(); // SIMULA UN CLIC AL BOTO DE GUARDAR
                    }
                }
            }
        });



        // CODI DEL CLIC AL BOTO INSERTAR
        insertarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de INSERTAR");

                if (insertCorrecte()) {
                    try {
                        if (!nomRepetit()) {
                            insertAlumne();
                        }
                    } catch (ParseException ex) {
                        campNota.setSelectionStart(0);
                        campNota.setSelectionEnd(campNota.getText().length());
                        campNota.requestFocus();
                    }
                }
            }

            /**
             * @return si algun dels camps està buit, mostra un missatge d'error.
             */
            private boolean insertCorrecte() {
                if (algunCampBuit()){
                    JOptionPane.showMessageDialog(null,"No olvidis emplenar tots els camps.", "Avís", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
                return true;
            }

            /**
             * @return si el nom està repetit, mostra un missatge d'error.
             */
            private boolean nomRepetit() {
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (campNom.getText().equals(model.getValueAt(i, 0))) {
                        JOptionPane.showMessageDialog(null, "Aquest nom ja esta inscrit a la taula, canvial.", "Error", JOptionPane.ERROR_MESSAGE);
                        campNom.setSelectionStart(0);
                        campNom.setSelectionEnd(campNom.getText().length());
                        campNom.requestFocus();
                        return true;
                    }
                }
                return false;
            }

            /**
             * @return si tot està correcte, insereix un nou alumne a la taula.
             */
            private void insertAlumne() throws ParseException {
                Double nota = parsearIVerificarNota(campNota.getText());
                model.addRow(new Object[]{campNom.getText(), Integer.valueOf(campEdat.getText()), nota, SI_CheckBox.isSelected()});
                JOptionPane.showMessageDialog(null,"Has inscrit un nou alumne","Inscripció correcta",JOptionPane.INFORMATION_MESSAGE);
                llimpiarCamps();
            }
        });



        // SELECCIÓ DELS ELEMENTS DE LA TAULA
        taula.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e the event to be processed
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int filaSel = taula.getSelectedRow();

                if (filaSel != -1) {
                    emplenarCamps(filaSel);
                } else {
                    llimpiarCamps();
                }
            }

            /**
             * @return Omple els camps amb les dades de la fila seleccionada.
             */
            private void emplenarCamps(int fila) {
                campNom.setText(taula.getValueAt(fila, 0).toString());
                campEdat.setText(taula.getValueAt(fila, 1).toString());
                campNota.setText(taula.getValueAt(fila, 2).toString());
                SI_CheckBox.setSelected((Boolean) model.getValueAt(fila, 3));
            }
        });



        // CODI DEL BOTO BORRAR
        borrarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de BORRAR");
                int filaSel = taula.getSelectedRow();
                if(filaSel!=-1){
                    model.removeRow(filaSel);
                    llimpiarCamps();
                }
                else JOptionPane.showMessageDialog(null, "Per borrar una fila l'has de seleccionar a la taula", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        // CODI DEL BOTO MODIFICAR
        modificarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha MODIFICAT un insert");
                int filaSel = taula.getSelectedRow();
                if(filaSel != -1){
                    if (algunCampBuit()) {
                        JOptionPane.showMessageDialog(null,"No olvidis emplenar tots els camps.", "Avís", JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        actualitzaFila();
                    }
                }
                else JOptionPane.showMessageDialog(null, "Per modificar una fila l'has de seleccionar a la taula", "Error", JOptionPane.ERROR_MESSAGE);
            }

            /**
             * @return Actualitza la fila seleccionada amb les dades dels camps.
             */
            private void actualitzaFila() {
                int filaSel = taula.getSelectedRow();
                model.removeRow(filaSel);
                model.insertRow(filaSel, new Object[]{campNom.getText(), Integer.valueOf(campEdat.getText()), Double.valueOf(campNota.getText()), SI_CheckBox.isSelected()});
                llimpiarCamps();
            }
        });



        // TRACTAR EXCEPCIONS, TRY/CATCH/FINALY
            // EN AQUEST CAS DETERMINEM QUE S'HA D'INTRODUIR UNA EDAT VALIDA
        campEdat.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             * @return has d'introduir una edat correcta.
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                try {
                    int edat=Integer.valueOf(campEdat.getText());
                    if (edat<1 || edat>100) throw new NumberFormatException();
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null,"Has d'introduir una edat correcta.", "Avís", JOptionPane.WARNING_MESSAGE);
                    campEdat.setSelectionStart(0);
                    campEdat.setSelectionEnd(campEdat.getText().length());
                    campEdat.requestFocus();
                }
            }
        });
            // EN AQUEST CAS DETERMINEM QUE S'HA D'INTRODUIR UNA NOTA VALIDA (DE L'0 AL 10)
        campNota.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             * @return has d'introduir una nota correcta.
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                try {
                    parsearIVerificarNota(campNota.getText());
                }catch (ParseException ex){
                    JOptionPane.showMessageDialog(null,"Has d'introduir una nota correcta i si te decimals separar-la per comes.", "Avís", JOptionPane.WARNING_MESSAGE);
                    campNota.setSelectionStart(0);
                    campNota.setSelectionEnd(campNota.getText().length());
                    campNota.requestFocus();
                }
            }
        });
            // EN AQUEST CAS DETERMINEM QUE S'HA D'INTRODUIR UNA NOM VALID ON NOMES PERMETRÀ CARACTERS
        campNom.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component loses the keyboard focus.
             *
             * @param e
             * @return has d'introduir un nom correcte.
             */
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);

                try {
                    String nom=(campNom.getText());
                    if (nom.matches(".*\\d.*")) throw new NumberFormatException();
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null,"No pots introduir cap numero en aquest camp.", "Avís", JOptionPane.WARNING_MESSAGE);
                    campNom.setSelectionStart(0);
                    campNom.setSelectionEnd(campNom.getText().length());
                    campNom.requestFocus();
                }
            }
        });



        // CODI DEL BOTO GUARDAR
        guardarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             * @return Guarda les dades a un fitxer.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de GUARDAR");
                botoGuardarPresionat = true;

                if (model.getRowCount() >= 0) {
                    ObjectOutputStream sortida = null;
                    try {
                        sortida = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
                        for (int i = 0; i < model.getRowCount(); i++) {
                            AlumnePojo alumne = new AlumnePojo(model.getValueAt(i, 0).toString(), Integer.valueOf(model.getValueAt(i, 1).toString()), Double.valueOf(model.getValueAt(i, 2).toString()), (Boolean) model.getValueAt(i, 3));
                            sortida.writeObject(alumne);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error al guardar les dades");
                    } finally {
                        try {
                            if (sortida != null) sortida.close();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error al tancar el fitxer de sortida");
                        }
                    }
                }
            }
        });



        // CODI DEL BOTO EXPORTAR
        exportarButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             * @return Exporta les dades a un fitxer de text.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("S'ha clicat el boto de EXPORTAR");

                if (model.getRowCount() > 0) {
                    File arxiu = seleccionarArxiu();
                    if (arxiu != null) {
                        PrintWriter writer = crearPrintWriter(arxiu);
                        if (writer != null) {
                            escriureFiles(writer);
                            JOptionPane.showMessageDialog(null, "Fitxer guardat amb exit amb el nom: " + arxiu.getName() +  "\n" + "A la ruta: " + arxiu.getAbsolutePath());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi han dades per exportar.","Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            /**
             * @return Retorna el fitxer seleccionat.
             */
            private File seleccionarArxiu() {
                JFileChooser fitxerElegit = new JFileChooser();
                fitxerElegit.setCurrentDirectory(new File("./src/main/resources/"));
                fitxerElegit.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fitxerElegit.setDialogTitle("Guardar arxiu de sortida");

                int userSelection = fitxerElegit.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File arxiu = fitxerElegit.getSelectedFile();
                    if (!arxiu.getAbsolutePath().endsWith(".txt")) {
                        arxiu = new File(arxiu.getAbsolutePath() + ".txt");
                    }
                    return arxiu;
                }
                return null;
            }

            /**
             * @param arxiu Fitxer on es guardarà la informació.
             * @return Retorna un PrintWriter per a escriure les dades al fitxer.
             */
            private PrintWriter crearPrintWriter(File arxiu) {
                try {
                    return new PrintWriter(new BufferedWriter(new FileWriter(arxiu)));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al crear el fitxer de sortida.");
                    return null;
                }
            }

            /**
             * @param writer PrintWriter per a escriure les dades al fitxer.
             * @return Escriu les dades al fitxer.
             */
            private void escriureFiles(PrintWriter writer) {
                try {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        writer.println("ENTRADA " + (i + 1));
                        writer.println("Nom i cognom: " + model.getValueAt(i, 0));
                        writer.println("Edat: " + model.getValueAt(i, 1));
                        writer.println("Nota: " + model.getValueAt(i, 2));
                        writer.println("FCT: " + model.getValueAt(i, 3));
                        writer.println("--------------------");
                    }
                } finally {
                    writer.close();
                }
            }
        });



        // CODI PER A LLEGIR LES DADES DEL FITXER
        if (f.exists() && !f.isDirectory()) {
            ObjectInputStream entrada = null;
            try {
                entrada = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
                while (true) {
                    AlumnePojo alumne = (AlumnePojo) entrada.readObject();
                    model.addRow(new Object[]{alumne.getNomCognom(), alumne.getEdat(), alumne.getNota(), alumne.isFct()});
                }
            } catch (EOFException ex) {
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Error al llegir el fitxer");
            } finally {
                try {
                    if (entrada != null) entrada.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al tancar el fitxer de lectura");
                }
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Alumne();
            }
        });
    }
}