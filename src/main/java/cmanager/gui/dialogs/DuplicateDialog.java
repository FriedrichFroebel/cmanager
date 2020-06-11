package cmanager.gui.dialogs;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.gui.ExceptionPanel;
import cmanager.gui.FrameHelper;
import cmanager.gui.components.Logo;
import cmanager.list.CacheListModel;
import cmanager.oc.OcUtil;
import cmanager.oc.OutputInterface;
import cmanager.oc.ShadowList;
import cmanager.okapi.User;
import cmanager.settings.Settings;
import cmanager.util.DesktopUtil;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class DuplicateDialog extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JFrame THIS = this;
    private final JPanel contentPanel = new JPanel();
    private final JTree tree;
    private final DefaultMutableTreeNode rootNode;
    private final JProgressBar progressBar;
    private String selectedUrl;
    private final JButton buttonUrl;
    private final JLabel labelCandidates;

    private final AtomicBoolean stopBackgroundThread = new AtomicBoolean(false);
    private Thread backgroundThread = null;

    private final List<GeocacheLog> logsCopied = new ArrayList<>();
    private ShadowList shadowList = null;

    private final boolean isCopyDialog;

    /** Create the dialog. */
    public DuplicateDialog(
            final CacheListModel cacheListModel, final User user, final String uuid) {
        // We are passing `user = null` and `uuid = null` for "Find on OC", so we are able to detect
        // the usage type of this dialog with this condition.
        isCopyDialog = uuid != null;

        setResizable(true);
        this.setMinimumSize(new Dimension(600, 300));
        Logo.setLogo(this);

        setTitle("Duplicate Finder");
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new CardLayout(0, 0));

        final JPanel panelProgress = new JPanel();
        panelProgress.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.add(panelProgress, "name_449323361533634");
        panelProgress.setLayout(new BorderLayout(0, 0));

        final JPanel panelBorder = new JPanel();
        panelBorder.setBorder(new EmptyBorder(20, 5, 5, 5));
        panelProgress.add(panelBorder, BorderLayout.NORTH);
        panelBorder.setLayout(new BorderLayout(0, 0));

        progressBar = new JProgressBar();
        panelBorder.add(progressBar);

        // Create the root node.
        rootNode = new DefaultMutableTreeNode("Root");

        final JPanel panelTree = new JPanel();
        contentPanel.add(panelTree, "2");
        panelTree.setLayout(new BorderLayout(0, 0));

        final JPanel panelUrl = new JPanel();
        panelTree.add(panelUrl, BorderLayout.SOUTH);
        panelUrl.setLayout(new BorderLayout(0, 0));

        buttonUrl = new JButton("");
        buttonUrl.setBorderPainted(false);
        buttonUrl.setOpaque(false);
        buttonUrl.setContentAreaFilled(false);
        buttonUrl.addActionListener(
                actionEvent -> {
                    if (selectedUrl != null) {
                        DesktopUtil.openUrl(selectedUrl);
                    }
                });
        panelUrl.add(buttonUrl);

        final JPanel panelClipboard = new JPanel();
        panelUrl.add(panelClipboard, BorderLayout.SOUTH);
        panelClipboard.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        final JButton buttonClipboard = new JButton("Export all as text to clipboard");
        panelClipboard.add(buttonClipboard);
        buttonClipboard.addActionListener(actionEvent -> copyToClipboard());

        tree = new JTree(rootNode);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new CacheTreeSelectionListener());
        tree.addMouseListener(new CacheMouseAdapter(uuid));

        JScrollPane scrollPaneTree =
                new JScrollPane(
                        tree,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelTree.add(scrollPaneTree);

        final JPanel panelCopyMessage = new JPanel();
        panelTree.add(panelCopyMessage, BorderLayout.NORTH);
        if (!isCopyDialog) {
            panelCopyMessage.setVisible(false);
        }
        panelCopyMessage.setLayout(new BorderLayout(0, 0));

        final JLabel labelDoubleClick = new JLabel("Double Click an OC cache to open copy dialog.");
        labelDoubleClick.setHorizontalAlignment(SwingConstants.CENTER);
        panelCopyMessage.add(labelDoubleClick);

        final JPanel panelBottom = new JPanel();
        getContentPane().add(panelBottom, BorderLayout.SOUTH);
        panelBottom.setLayout(new BorderLayout(0, 0));

        final JPanel panelButtonOk = new JPanel();
        panelBottom.add(panelButtonOk, BorderLayout.EAST);

        final JButton buttonOk = new JButton("Dismiss");
        panelButtonOk.add(buttonOk);
        buttonOk.setHorizontalAlignment(SwingConstants.RIGHT);
        buttonOk.addActionListener(
                actionEvent -> {
                    THIS.setVisible(false);
                    if (backgroundThread != null) {
                        stopBackgroundThread.set(true);
                    }
                    THIS.dispose();
                });
        getRootPane().setDefaultButton(buttonOk);

        final JPanel panelDuplicateCount = new JPanel();
        panelBottom.add(panelDuplicateCount, BorderLayout.WEST);

        labelCandidates = new JLabel("0");
        panelDuplicateCount.add(labelCandidates);

        final JLabel labelHits = new JLabel("Candidates");
        panelDuplicateCount.add(labelHits);

        backgroundThread = new Thread(() -> findDuplicates(cacheListModel, user, uuid));
        backgroundThread.start();
    }

    private void switchCards() {
        final CardLayout cardLayout = (CardLayout) (contentPanel.getLayout());
        cardLayout.show(contentPanel, "2");
    }

    private void copyToClipboard() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < rootNode.getChildCount(); i++) {
            final DefaultMutableTreeNode mutableTreeNode =
                    (DefaultMutableTreeNode) rootNode.getChildAt(i);
            final Geocache geocache = (Geocache) mutableTreeNode.getUserObject();
            stringBuilder.append(geocache.toString()).append(System.lineSeparator());

            for (int j = 0; j < mutableTreeNode.getChildCount(); j++) {
                final DefaultMutableTreeNode child =
                        (DefaultMutableTreeNode) mutableTreeNode.getChildAt(j);
                final Geocache geocache2 = (Geocache) child.getUserObject();
                stringBuilder
                        .append("  ")
                        .append(geocache2.toString())
                        .append(System.lineSeparator());
            }
            stringBuilder.append(System.lineSeparator());
        }

        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(stringBuilder.toString()), null);
    }

    private void findDuplicates(
            final CacheListModel cacheListModel, final User user, final String uuid) {
        try {
            // Update local copy of shadow list and load it.
            ShadowList.updateShadowList();
            shadowList = ShadowList.loadShadowList();

            OcUtil.findOnOc(
                    stopBackgroundThread,
                    cacheListModel,
                    new OutputInterface() {
                        public void setProgress(Integer count, Integer max) {
                            progressBar.setMaximum(max);
                            progressBar.setValue(count);
                            progressBar.setString(count.toString() + "/" + max.toString());
                            progressBar.setStringPainted(true);
                        }

                        private Geocache lastGc = null;
                        private DefaultMutableTreeNode lastNode = null;
                        private Integer candidates = 0;

                        public void match(Geocache gc, Geocache oc) {
                            // Make sure that for the copy dialog at least one log of the specified
                            // user is present.
                            if (isCopyDialog && !gc.hasFoundLogByGcUser()) {
                                return;
                            }

                            candidates++;
                            labelCandidates.setText(candidates.toString());

                            if (gc != lastGc) {
                                lastGc = gc;
                                lastNode = new DefaultMutableTreeNode(gc);
                                rootNode.add(lastNode);
                            }
                            lastNode.add(new DefaultMutableTreeNode(oc));
                        }
                    },
                    user,
                    uuid,
                    shadowList);
            switchCards();

            if (stopBackgroundThread.get()) {
                return;
            }

            // Sort.
            final List<DefaultMutableTreeNode> sortedList = new ArrayList<>();
            final List<DefaultMutableTreeNode> list = new ArrayList<>();

            // Get all entries.
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                list.add((DefaultMutableTreeNode) rootNode.getChildAt(i));
            }
            rootNode.removeAllChildren();

            // Sort.
            final String gcUsername = Settings.getString(Settings.Key.GC_USERNAME);
            while (!list.isEmpty()) {
                DefaultMutableTreeNode next = null;

                for (final DefaultMutableTreeNode current : list) {
                    if (next == null) {
                        next = current;
                    } else {
                        final Geocache nextGeocache = (Geocache) next.getUserObject();
                        final Geocache currentGeocache = (Geocache) current.getUserObject();

                        GeocacheLog nextLog = null;
                        for (final GeocacheLog log : nextGeocache.getLogs()) {
                            if (log.isAuthor(gcUsername) && log.isFoundLog()) {
                                nextLog = log;
                                break;
                            }
                        }

                        GeocacheLog currentLog = null;
                        for (final GeocacheLog log : currentGeocache.getLogs()) {
                            if (log.isAuthor(gcUsername) && log.isFoundLog()) {
                                currentLog = log;
                                break;
                            }
                        }

                        if (currentLog == null) {
                            continue;
                        }
                        if (nextLog == null) {
                            next = current;
                            continue;
                        }

                        if (currentLog.getDate().isAfter(nextLog.getDate())) {
                            next = current;
                        }
                    }
                }
                list.remove(next);
                sortedList.add(next);
            }

            // Add entries.
            for (int i = 0; i < sortedList.size(); i++) {
                rootNode.insert(sortedList.get(i), i);
            }

            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
            tree.setRootVisible(false);

            if (tree.getRowCount() == 0) {
                tree.setVisible(false);
            }
        } catch (Throwable throwable) {
            // Since Thread.stop() is used, the threads will most likely
            // complain in weird ways. We do not care about these
            // exceptions.
            if (!stopBackgroundThread.get()) {
                ExceptionPanel.showErrorDialog(THIS, throwable);
            }
            THIS.setVisible(false);
        }
    }

    private class CacheTreeSelectionListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            final DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }

            final Object userObject = node.getUserObject();
            if (userObject instanceof Geocache) {
                final Geocache geocache = (Geocache) userObject;
                url2Button(geocache.getUrl());
            }
        }

        private void url2Button(String url) {
            selectedUrl = url;
            buttonUrl.setText("<HTML><FONT color=\"#000099\"><U>" + url + "</U></FONT></HTML>");
        }
    }

    private class CacheMouseAdapter extends MouseAdapter {

        private final String uuid;

        public CacheMouseAdapter(String uuid) {
            this.uuid = uuid;
        }

        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() >= 2) {
                final DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }

                final Object userObject = node.getUserObject();
                if (userObject instanceof Geocache) {
                    final Geocache oc = (Geocache) userObject;

                    if (uuid != null && oc.isOc()) {
                        final DefaultMutableTreeNode parent =
                                (DefaultMutableTreeNode) node.getParent();
                        final Geocache gc = (Geocache) parent.getUserObject();

                        try {
                            final CopyLogDialog copyLogDialog =
                                    new CopyLogDialog(gc, oc, logsCopied, shadowList);
                            copyLogDialog.setLocationRelativeTo(THIS);
                            FrameHelper.showModalFrame(copyLogDialog, THIS);
                        } catch (Throwable throwable) {
                            ExceptionPanel.showErrorDialog(THIS, throwable);
                        }
                    }
                }
            }
        }
    }
}
