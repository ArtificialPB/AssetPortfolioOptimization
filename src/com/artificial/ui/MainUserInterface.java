package com.artificial.ui;

import com.artificial.MarketIndex;
import com.artificial.Portfolio;
import com.artificial.RiskFreeAsset;
import com.artificial.Stock;
import org.math.plot.Plot2DPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class MainUserInterface extends JFrame {
    private static final Map<String, Stock> loadedStocks = new HashMap<>();
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    private final Portfolio portfolio = new Portfolio();
    private MarketIndex selectedMarketIndex = MarketIndex.SP_500;
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel generalPane;
    private JPanel panel1;
    private NamedTextField assetSymbolText;
    private JButton assetAddButton;
    private JLabel label1;
    private CustomPresetCombo riskFreeAssetCombo;
    private JLabel label2;
    private CustomPresetCombo marketIndexCombo;
    private JPanel panel3;
    private JButton computePortfolioButton;
    private JPanel panel2;
    private JScrollPane stocksAddedScrollPane;
    private JTable stocksAddedTable;
    private JScrollPane graphScrollPane;

    public MainUserInterface() {
        NUMBER_FORMAT.setMaximumFractionDigits(2);
        NUMBER_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
        initComponents();
        this.setVisible(true);
    }

    private void assetAddButtonActionPerformed() {
        final String symbol = assetSymbolText.getText().toUpperCase();
        assetSymbolText.clear();
        if (symbol.equalsIgnoreCase("null")) {
            return;
        }
        final Stock stock = new Stock(symbol);
        if (!stock.isLoaded()) {
            JOptionPane.showMessageDialog(this, "Failed to load asset with symbol " + symbol + "!", "Error loading asset", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final DefaultTableModel model = (DefaultTableModel) stocksAddedTable.getModel();
        model.addRow(new Object[]{stock.getSymbol(), stock.getName(), stock.getBeta(selectedMarketIndex), "N/A"});
        loadedStocks.put(symbol, stock);
        portfolio.addStock(stock);
    }

    private void marketIndexComboActionPerformed() {
        final ItemWrapper selected = (ItemWrapper) marketIndexCombo.getSelectedItem();
        if (selected.getObject() instanceof MarketIndex) {
            final MarketIndex last = selectedMarketIndex;
            final MarketIndex curr = (MarketIndex) selected.getObject();
            if (last.getSymbol().equals(curr.getSymbol())) {
                return;
            }
            selectedMarketIndex = curr;
            final DefaultTableModel model = (DefaultTableModel) stocksAddedTable.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                final String symbol = (String) model.getValueAt(row, 0);
                final Stock stock = loadedStocks.get(symbol);
                model.setValueAt(stock.getBeta(selectedMarketIndex), row, 2);
            }
        }
    }

    private void computePortfolioButtonActionPerformed() {
        final Plot2DPanel plot = new Plot2DPanel();
        plot.setAxisLabels("Std. Deviation", "Return");
        portfolio.efficientFrontier(plot);
        graphScrollPane.setViewportView(plot);
        final ItemWrapper riskFree = (ItemWrapper) riskFreeAssetCombo.getSelectedItem();
        if (riskFree.getObject() instanceof RiskFreeAsset) {
            portfolio.optimizationOptimalRiskyPortfolio((RiskFreeAsset) riskFree.getObject(), plot);
            final DefaultTableModel tableModel = (DefaultTableModel) stocksAddedTable.getModel();
            final Map<Stock, Double> stockWeights = portfolio.getStockWeights();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                final String symbol = (String) tableModel.getValueAt(i, 0);
                final double weight = stockWeights.get(loadedStocks.get(symbol)) * 100;
                tableModel.setValueAt(NUMBER_FORMAT.format(weight) + "%", i, 3);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        generalPane = new JPanel();
        panel1 = new JPanel();
        assetSymbolText = new NamedTextField();
        assetAddButton = new JButton();
        label1 = new JLabel();
        riskFreeAssetCombo = new CustomPresetCombo();
        for (RiskFreeAsset riskFree : RiskFreeAsset.VALUES) {
            if (riskFree.isLoaded()) {
                riskFreeAssetCombo.addItem(new ItemWrapper(riskFree));
            }
        }
        riskFreeAssetCombo.setSelectedIndex(0);
        label2 = new JLabel();
        marketIndexCombo = new CustomPresetCombo();
        for (MarketIndex marketIndex : MarketIndex.VALUES) {
            if (marketIndex.isLoaded()) {
                if (selectedMarketIndex == null) selectedMarketIndex = marketIndex;
                marketIndexCombo.addItem(new ItemWrapper(marketIndex));
            }
        }
        marketIndexCombo.setSelectedIndex(0);
        panel3 = new JPanel();
        computePortfolioButton = new JButton();
        panel2 = new JPanel();
        stocksAddedScrollPane = new JScrollPane();
        stocksAddedTable = new JTable();
        graphScrollPane = new JScrollPane();

        //======== this ========
        setTitle("Portfolio optimization");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setName("this");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        //======== generalPane ========
        {
            generalPane.setName("generalPane");
            generalPane.setLayout(new BoxLayout(generalPane, BoxLayout.Y_AXIS));

            //======== panel1 ========
            {
                panel1.setName("panel1");
                panel1.setLayout(new FlowLayout());

                //---- assetSymbolText ----
                assetSymbolText.setPreferredSize(new Dimension(70, 24));
                assetSymbolText.setSlotName("Symbol");
                assetSymbolText.setName("assetSymbolText");
                panel1.add(assetSymbolText);

                //---- assetAddButton ----
                assetAddButton.setText("Add Asset");
                assetAddButton.setName("assetAddButton");
                assetAddButton.addActionListener(e -> assetAddButtonActionPerformed());
                panel1.add(assetAddButton);

                //---- label1 ----
                label1.setText("Risk-free asset");
                label1.setName("label1");
                panel1.add(label1);

                //---- riskFreeAssetCombo ----
                riskFreeAssetCombo.setName("riskFreeAssetCombo");
                panel1.add(riskFreeAssetCombo);

                //---- label2 ----
                label2.setText("Market Index");
                label2.setName("label2");
                panel1.add(label2);

                //---- marketIndexCombo ----
                marketIndexCombo.setName("marketIndexCombo");
                marketIndexCombo.addActionListener(e -> marketIndexComboActionPerformed());
                panel1.add(marketIndexCombo);
            }
            generalPane.add(panel1);

            //======== panel3 ========
            {
                panel3.setName("panel3");
                panel3.setLayout(new FlowLayout());

                //---- computePortfolioButton ----
                computePortfolioButton.setText("Compute optimal risky portfolio");
                computePortfolioButton.setName("computePortfolioButton");
                computePortfolioButton.addActionListener(e -> computePortfolioButtonActionPerformed());
                panel3.add(computePortfolioButton);
            }
            generalPane.add(panel3);
        }
        contentPane.add(generalPane);

        //======== panel2 ========
        {
            panel2.setName("panel2");
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

            //======== stocksAddedScrollPane ========
            {
                stocksAddedScrollPane.setName("stocksAddedScrollPane");

                //---- stocksAddedTable ----
                stocksAddedTable.setModel(new DefaultTableModel(
                        new Object[][]{
                        },
                        new String[]{
                                "Symbol", "Name", "Beta", "Weight"
                        }
                ) {
                    Class<?>[] columnTypes = new Class<?>[]{
                            String.class, String.class, Double.class, String.class
                    };
                    boolean[] columnEditable = new boolean[]{
                            false, false, false, true
                    };

                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }

                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                stocksAddedTable.setName("stocksAddedTable");
                final String s = "delete";
                final KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
                stocksAddedTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delete, s);
                stocksAddedTable.getActionMap().put(s, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final int[] index = stocksAddedTable.getSelectedRows();
                        if (index.length == 0) return;
                        final DefaultTableModel model = ((DefaultTableModel) stocksAddedTable.getModel());
                        for (int i : index) {
                            final String symbol = (String) model.getValueAt(i, 0);
                            final Stock s = loadedStocks.remove(symbol);
                            if (s != null) {
                                portfolio.removeStock(s);
                            }
                            model.removeRow(i);
                        }
                    }
                });
                stocksAddedScrollPane.setViewportView(stocksAddedTable);
            }
            panel2.add(stocksAddedScrollPane);

            //======== graphScrollPane ========
            {
                graphScrollPane.setName("graphScrollPane");
                graphScrollPane.setViewportView(new Plot2DPanel());
            }
            panel2.add(graphScrollPane);
        }
        contentPane.add(panel2);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
