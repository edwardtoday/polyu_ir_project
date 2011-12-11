/*
 * QueryIndexView.java
 */
package queryindex;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The application's main frame.
 */
public class QueryIndexView extends FrameView {

    private File defaultSearchPath = new File(System.getProperty("user.dir")).getParentFile();

    public QueryIndexView(final SingleFrameApplication app) {
        super(app);

        initComponents();

        // Highlight text
        Style style = jTextPaneViewer.addStyle("Highlight", null);
        StyleConstants.setBackground(style, Color.yellow);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            final JFrame mainFrame = QueryIndexApp.getApplication().getMainFrame();
            aboutBox = new QueryIndexAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        QueryIndexApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTextFieldDatapath = new javax.swing.JTextField();
        jLabelDatapath = new javax.swing.JLabel();
        jButtonBrowser = new javax.swing.JButton();
        jLabelQuery = new javax.swing.JLabel();
        jTextFieldQuery = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListResult = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPaneViewer = new javax.swing.JTextPane();
        jButtonChooseStopList = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();

        mainPanel.setMinimumSize(new java.awt.Dimension(450, 320));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setSize(new java.awt.Dimension(639, 377));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(queryindex.QueryIndexApp.class).getContext().getResourceMap(QueryIndexView.class);
        jTextFieldDatapath.setText(resourceMap.getString("jTextFieldDatapath.text")); // NOI18N
        jTextFieldDatapath.setName("jTextFieldDatapath"); // NOI18N

        jLabelDatapath.setText(resourceMap.getString("jLabelDatapath.text")); // NOI18N
        jLabelDatapath.setName("jLabelDatapath"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(queryindex.QueryIndexApp.class).getContext().getActionMap(QueryIndexView.class, this);
        jButtonBrowser.setAction(actionMap.get("BrowseDatapath")); // NOI18N
        jButtonBrowser.setText(resourceMap.getString("jButtonBrowse.text")); // NOI18N
        jButtonBrowser.setName("jButtonBrowse"); // NOI18N

        jLabelQuery.setText(resourceMap.getString("jLabelQuery.text")); // NOI18N
        jLabelQuery.setName("jLabelQuery"); // NOI18N

        jTextFieldQuery.setText(resourceMap.getString("jTextFieldQuery.text")); // NOI18N
        jTextFieldQuery.setName("jTextFieldQuery"); // NOI18N

        jButtonSearch.setAction(actionMap.get("SearchQuery")); // NOI18N
        jButtonSearch.setText(resourceMap.getString("jButtonSearch.text")); // NOI18N
        jButtonSearch.setName("jButtonSearch"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jListResult.setFont(resourceMap.getFont("jListResults.font")); // NOI18N
        jListResult.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListResult.setMaximumSize(new java.awt.Dimension(300, 25));
        jListResult.setName("jListResults"); // NOI18N
        jListResult.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListResultValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListResult);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextPaneViewer.setText(resourceMap.getString("jTextPaneViewer.text")); // NOI18N
        jTextPaneViewer.setName("jTextPaneViewer"); // NOI18N
        jScrollPane2.setViewportView(jTextPaneViewer);

        jButtonChooseStopList.setAction(actionMap.get("BrowseStoplist")); // NOI18N
        jButtonChooseStopList.setText(resourceMap.getString("jButtonChooseStopList.text")); // NOI18N
        jButtonChooseStopList.setName("jButtonChooseStopList"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabelQuery)
                            .add(jLabelDatapath))
                        .add(18, 18, 18)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextFieldDatapath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                            .add(jTextFieldQuery, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                                .add(jButtonBrowser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonChooseStopList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jButtonSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 237, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelDatapath)
                    .add(jTextFieldDatapath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonBrowser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonChooseStopList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelQuery)
                    .add(jTextFieldQuery, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)))
        );

        menuBar.setMinimumSize(new java.awt.Dimension(400, 1));
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setMinimumSize(new java.awt.Dimension(400, 0));
        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 650, Short.MAX_VALUE)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jListResultValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListResultValueChanged
        // TODO add your handling code here:
        if (evt.getValueIsAdjusting() == false) {
            jTextPaneViewer.setText(null);
            final int selectedIndex = jListResult.getSelectedIndex();
            if (selectedIndex != -1) {
                final Indexer indexer = Indexer.getInstance();
                //Selection, update viewer.
                File f = indexer.getCachedFiles()[selectedIndex];
                statusMessageLabel.setText("Document weight = " + indexer.getCachedWeights()[selectedIndex]);
                StyledDocument doc = jTextPaneViewer.getStyledDocument();

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(f));

                    char[] buffer = new char[16384]; // Read 64K characters at a time
                    int len; // How many chars read each time
                    while ((len = br.read(buffer)) != -1) { // Read a batch of chars
                        String s = new String(buffer, 0, len); // Convert to a string
                        doc.insertString(doc.getLength(), s, null);
                    }
                    int firstHighlight = Integer.MAX_VALUE;
                    // Highlight
                    try {
                        String text = doc.getText(0, doc.getLength()).toLowerCase();
                        for (String pattern : indexer.getToHighlight()) {
                            int pos = 0;
                            // Search for pattern
                            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                                //Check the character before and after this pattern
                                char shouldBeSplitter = text.charAt(pos - 1);
                                char shouldAlsoBeSplitter = text.charAt(pos + pattern.length());
                                if ((shouldBeSplitter == ' '
                                        || shouldBeSplitter == '.'
                                        || shouldBeSplitter == '\n')
                                        && (shouldAlsoBeSplitter == ' '
                                        || shouldAlsoBeSplitter == '.'
                                        || shouldAlsoBeSplitter == '\n')) {
                                    firstHighlight = pos < firstHighlight ? pos : firstHighlight;
                                    doc.setCharacterAttributes(pos, pattern.length(),
                                            jTextPaneViewer.getStyle("Highlight"), true);
                                }
                                pos += pattern.length();
                            }
                        }
                    } catch (BadLocationException e) {
                    }
                    jTextPaneViewer.setCaretPosition(firstHighlight); // Go to first hightlight position
                } // Display messages if something goes wrong
                catch (BadLocationException ex) {
                    Logger.getLogger(QueryIndexView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException e) {
                    statusMessageLabel.setText(e.getClass().getName() + ": " + e.getMessage());
                } // Always be sure to close the input stream!
                finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }//GEN-LAST:event_jListResultValueChanged

    /**
     * Open a FileChooser to let the user select where the data files are.
     */
    @Action
    public Task BrowseDatapath() {
        return new BrowseDatapathTask(getApplication());
    }

    private class BrowseDatapathTask extends org.jdesktop.application.Task<Object, Void> {

        private boolean doIndex = false;

        BrowseDatapathTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to BrowseDatapathTask fields, here.
            super(app);
            JFileChooser fc = new JFileChooser(defaultSearchPath);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(QueryIndexApp.getApplication().getMainFrame());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                jTextFieldDatapath.setText(file.getAbsolutePath());
                statusMessageLabel.setText(Messages.pathSpecified);
                Indexer.getInstance().setDataPath(file.getAbsolutePath());
                doIndex = true;
            } else {
                statusMessageLabel.setText(Messages.specifyDatapath);
            }
            statusAnimationLabel.setText("Creating index...");
        }

        @Override
        protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            if (doIndex) {
                Indexer.getInstance().clearAll();
                Indexer.getInstance().RecursiveLoad(new File(jTextFieldDatapath.getText()));
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            if (doIndex) {
                statusAnimationLabel.setText("Indexed " + Indexer.getInstance().getNum_of_documents() + " documents.");
            }

        }
    }

    /**
     * Search the query in the index.
     * @return
     */
    @Action
    public Task SearchQuery() {
        return new SearchQueryTask(getApplication());
    }

    private class SearchQueryTask extends org.jdesktop.application.Task<Object, Void> {

        SearchQueryTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to SearchQueryTask fields, here.
            super(app);
            statusAnimationLabel.setText("Searching...");
            jListResult.setListData(new String[0]);
        }

        @Override
        protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            String query = jTextFieldQuery.getText();
            if (Indexer.getInstance().TestLogic(query)){
                statusMessageLabel.setText(Messages.errorMixedAndOr);
            } else if (query.isEmpty()) {
                statusMessageLabel.setText(Messages.errorNullQuery);
            } else {
                Indexer.getInstance().SearchLogic(query);
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            statusAnimationLabel.setText(null);
            final String[] cachedFilenames = Indexer.getInstance().getCachedFilenames();
            if (cachedFilenames != null) {
                if (cachedFilenames.length == 0) {
                    statusMessageLabel.setText(Messages.errorFoundNothing);
                } else {
                    statusAnimationLabel.setText("Found " + cachedFilenames.length + " results.");
                    jListResult.setListData(cachedFilenames);
                    jListResult.setAutoscrolls(true);
                }
            }
        }
    }

    @Action
    public Task BrowseStoplist() {
        return new BrowseStoplistTask(getApplication());
    }

    private class BrowseStoplistTask extends org.jdesktop.application.Task<Object, Void> {

        private boolean loadStoplist = false;

        BrowseStoplistTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to BrowseStoplistTask fields, here.
            super(app);
            JFileChooser fc = new JFileChooser(defaultSearchPath);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fc.showOpenDialog(QueryIndexApp.getApplication().getMainFrame());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    statusAnimationLabel.setText("Loading stoplist...");
                    File file = fc.getSelectedFile();
                    statusMessageLabel.setText(Messages.stoplistSpecified + file.getName());
                    Indexer.getInstance().setStoplist(file);
                    loadStoplist = true;
                } catch (Exception ex) {
                    Logger.getLogger(QueryIndexView.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                statusMessageLabel.setText(Messages.stoplistSelectionCancelled);
            }
        }

        @Override
        protected Object doInBackground() {
            final Indexer indexer = Indexer.getInstance();
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            if (loadStoplist) {
                indexer.LoadStoplist();
            }
            if (indexer.getNum_of_documents() != 0) { // An index exists. Update it.
                indexer.clearAll();
                indexer.RecursiveLoad(new File(jTextFieldDatapath.getText()));
            }
            if (indexer.getCachedFilenames() != null
                    && indexer.getCachedFilenames().length != 0) {
                // A search already exists. Update it.
                SearchQuery();
            }

            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            if (loadStoplist) {
                final Indexer indexer = Indexer.getInstance();
                statusAnimationLabel.setText(indexer.getStopwords().size() + " stopwords loaded.");
                if (indexer.getCachedFilenames() != null
                        && indexer.getCachedFilenames().length != 0) {
                    // A search already exists. Update it.
                    statusMessageLabel.setText(Messages.resultUpdatedUsingStoplist);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowser;
    private javax.swing.JButton jButtonChooseStopList;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabelDatapath;
    private javax.swing.JLabel jLabelQuery;
    private javax.swing.JList jListResult;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldDatapath;
    private javax.swing.JTextField jTextFieldQuery;
    private javax.swing.JTextPane jTextPaneViewer;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}