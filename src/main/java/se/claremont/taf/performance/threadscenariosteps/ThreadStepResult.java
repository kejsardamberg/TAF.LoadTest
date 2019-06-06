package se.claremont.taf.performance.threadscenariosteps;

import se.claremont.taf.performance.gui.guiProfiles.TafLabel;
import se.claremont.taf.performance.gui.guiProfiles.TafPanel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class ThreadStepResult {
    public ThreadStepExecutionStatus status;
    public Date startTime;
    public List<String> messages;
    public Date stopTime;
    public String stepName;
    public List<ThreadStepResult> subResults;
    private TafPanel resultsDescriptionPanel;

    public ThreadStepResult(String stepName){
        subResults = new ArrayList<>();
        status = ThreadStepExecutionStatus.NOT_STARTED;
        messages = new ArrayList<>();
        this.stepName = stepName;
    }

    public void attachSubResult(ThreadStepResult result){
        subResults.add(result);
    }

    public void startExecution(){
        System.out.println("Starting execution of '" + stepName + "'.");
        status = ThreadStepExecutionStatus.RUNNING;
        startTime = new Date();
    }

    public void stopExeuction(){
        System.out.println("Stopping execution of '" + stepName + "'.");
        if(status == ThreadStepExecutionStatus.NOT_STARTED){
            messages.add("INTERNAL ERROR: ThreadStepResult should be started with startExecution() method.");
            status = ThreadStepExecutionStatus.FAILED;
        }
        if(status == ThreadStepExecutionStatus.RUNNING)
            status = ThreadStepExecutionStatus.PASSED;
        stopTime = new Date();
    }

    public void setStatus(ThreadStepExecutionStatus status){
        this.status = status;
    }

    public void addMessage(String message)
    {
        messages.add(message);
    }

    private void updateResultsView(ThreadStepResult result){
        if(this.resultsDescriptionPanel == null) return;
        resultsDescriptionPanel.removeAll();
        resultsDescriptionPanel.add(result.guiPanelView());
        resultsDescriptionPanel.revalidate();
        resultsDescriptionPanel.repaint();
    }

    public TafPanel getResultAsGuiTree(){
        Map<ThreadStepResult, DefaultMutableTreeNode> nodeMapper = new HashMap<ThreadStepResult, DefaultMutableTreeNode>();
        TafPanel panel = new TafPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(this);

        for(ThreadStepResult subResult : subResults){
            top.add(resultsSubNodes(subResult));
        }
        JTree tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.setCellRenderer(new ResultTreeRenderer());

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();

                if (node == null)
                    //Nothing is selected.
                    return;

                Object nodeInfo = node.getUserObject();
                ThreadStepResult result = (ThreadStepResult)nodeInfo;
                updateResultsView(result);
            }
        });
        panel.add(new JScrollPane(tree));

        resultsDescriptionPanel = new TafPanel();
        panel.add(resultsDescriptionPanel);
        return panel;
    }

    class ResultTreeRenderer implements TreeCellRenderer {
        private JLabel label;

        ResultTreeRenderer() {
            label = new JLabel();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object o = ((DefaultMutableTreeNode)value).getUserObject();
            if (o instanceof ThreadStepResult) {
                ThreadStepResult result = (ThreadStepResult) o;
                URL imageUrl = getClass().getResource(result.status.getResultsTreeIcon());
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(result.stepName);
            } else {
                System.out.println(o.getClass().getName());
                label.setIcon(null);
                label.setText("" + value);
            }
            return label;
        }
    }

    public TafPanel guiPanelView(){
        TafPanel resultsPanel = new TafPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.add(new TafLabel("Name: " + stepName));
        resultsPanel.add(new TafLabel("Start time: " + startTime));
        resultsPanel.add(new TafLabel("Stop time:  " + stopTime));
        resultsPanel.add(new TafLabel("Status: " + status.toString()));
        resultsPanel.add(new TafLabel("Messages:"));
        TafPanel messagesPanel = new TafPanel();
        for(String message : messages){
            messagesPanel.add(new TafLabel("<html><body>" + message.replace(System.lineSeparator(), "<br>") + "</body></html>"));
        }
        resultsPanel.add(new JScrollPane(messagesPanel));
        return resultsPanel;
    }

    protected DefaultMutableTreeNode resultsSubNodes(ThreadStepResult subResult){
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(subResult);
        for(ThreadStepResult subberResults : subResult.subResults){
            node.add(resultsSubNodes(subberResults));
        }
        return node;
    }

    @Override
    public String toString(){
        return stepName;
        //StringBuilder sb = new StringBuilder(stepName).append(" [").append(status).append("] ").append(String.join(", ", messages)).append(System.lineSeparator());
        //for (ThreadStepResult result : subResults){
        //    sb.append(result.toString()).append(System.lineSeparator());
        //}
        //return sb.toString();
    }
}
