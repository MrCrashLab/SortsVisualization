package ru.MrCrashLab;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Form extends JFrame {
    private final int frameWidth = 930;
    private final int frameHeight = 970;
    private final int imgWidth = 270;
    private final int imgHeight = 270;
    private final int numGroupsOnLine = 10;
    private final int numGroups = numGroupsOnLine * numGroupsOnLine;
    private final int assemblySpeed = 1;
    private final double slowSpeed = 0;
    private final String imgFilePath = "src/resources/mem-s-bagz-banni.jpg";

    private final Color BACKGROUND = new Color(52, 52, 52, 255);
    private final Color TEXT_COLOR = new Color(88, 161, 234);
    private final ImageFragment[][] imageFragments = new ImageFragment[9][numGroups];

    private final JPanel commonPanel = new JPanel();
    private final JPanel imagesPanel = new JPanel();
    private final JPanel[] imageAndTextPanels = new JPanel[9];

    private final GridBagLayout commonLayout = new GridBagLayout();

    private final GridLayout imagesLayout = new GridLayout(3, 3, 29, 4);
    private final GridBagConstraints commonGridBagConstraints = new GridBagConstraints();
    private BoxLayout imageAndTextLayout = new BoxLayout(imagesPanel, BoxLayout.X_AXIS);

    private final JButton sortButton = new JButton("Sort");
    private final JButton mixButton = new JButton("Mix");

    private JLabel[] imageLabels = new JLabel[9];
    private JLabel[] textLabels = new JLabel[9];
    private ImageIcon[] imageIcons = new ImageIcon[9];

    private Thread[] threads = new Thread[9];
    private Thread repaintThread;

    private int quickLeft = 0;
    private int quickRight = imageFragments[4].length - 1;
    private int quickCount = 0;

    private boolean mixFlag = true;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public Form() {
        Image image;
        try {
            image = ImageIO.read(new File(imgFilePath));
            image = image.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
            for (int i = 0; i < 9; i++) {
                imageIcons[i] = new ImageIcon(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 9; i++) {
            threads[i] = new Thread();
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(0, 0);
        this.setResizable(false);
        this.getContentPane().add(commonPanel);

        createImagesPanel();
        createCommonPanel();

        this.pack();
        this.setVisible(true);

        sortButton.addActionListener(e -> createAndStartThreads());
        mixButton.addActionListener(e -> shuffleImages());
    }

    private void createImagesPanel() {
        imagesPanel.setBackground(BACKGROUND);
        imagesPanel.setLayout(imagesLayout);
        createTextLabels();
        for (int i = 0; i < 9; i++) {
            imageAndTextPanels[i] = new JPanel();
            imageAndTextPanels[i].setBackground(BACKGROUND);
            imageAndTextLayout = new BoxLayout(imageAndTextPanels[i], BoxLayout.Y_AXIS);
            imageAndTextPanels[i].setLayout(imageAndTextLayout);

            imageLabels[i] = new JLabel(imageIcons[i]);
            imageLabels[i].setBackground(BACKGROUND);

            imageAndTextPanels[i].add(textLabels[i]);
            imageAndTextPanels[i].add(imageLabels[i]);
            imagesPanel.add(imageAndTextPanels[i]);
        }
    }

    private void createCommonPanel() {
        commonPanel.setBackground(BACKGROUND);
        commonPanel.setLayout(commonLayout);
        commonPanel.add(imagesPanel);
        commonPanel.setPreferredSize(new Dimension(frameWidth, frameHeight));
        commonPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 0, 25));
        commonGridBagConstraints.gridx = 0;
        commonGridBagConstraints.gridy = 0;
        commonGridBagConstraints.gridwidth = 3;
        commonGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        commonPanel.add(imagesPanel, commonGridBagConstraints);

        commonGridBagConstraints.gridx = 0;
        commonGridBagConstraints.gridy = 1;
        commonGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        commonPanel.add(Box.createVerticalStrut(10), commonGridBagConstraints);

        commonGridBagConstraints.gridx = 0;
        commonGridBagConstraints.gridy = 2;
        commonGridBagConstraints.gridwidth = 1;
        commonGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        commonPanel.add(sortButton, commonGridBagConstraints);

        commonGridBagConstraints.gridx = 1;
        commonGridBagConstraints.gridy = 2;
        commonGridBagConstraints.gridwidth = 1;
        commonGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        commonPanel.add(Box.createHorizontalStrut(600), commonGridBagConstraints);

        commonGridBagConstraints.gridx = 2;
        commonGridBagConstraints.gridy = 2;
        commonGridBagConstraints.gridwidth = 1;
        commonGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        commonPanel.add(mixButton, commonGridBagConstraints);
    }

    private void createTextLabels() {
        textLabels[0] = new JLabel("Bubble Sort");
        textLabels[1] = new JLabel("Shaker Sort");
        textLabels[2] = new JLabel("Comb Sort");
        textLabels[3] = new JLabel("Insertion Sort");
        textLabels[4] = new JLabel("Quick Sort");
        textLabels[5] = new JLabel("Gnome Sort");
        textLabels[6] = new JLabel("Selection Sort");
        textLabels[7] = new JLabel("Merge Sort");
        textLabels[8] = new JLabel("Bubble");
        for (int i = 0; i < 9; i++) {
            textLabels[i].setFont(new Font("Comfortaa", Font.BOLD, 25));
            textLabels[i].setBackground(BACKGROUND);
            textLabels[i].setForeground(TEXT_COLOR);
        }
    }

    private void createAndStartThreads() {
        if (!checkIsAliveThreads()) {
            threads[0] = new Thread(() -> bubbleSort());
            threads[1] = new Thread(() -> shakerSort());
            threads[2] = new Thread(() -> combSort());
            threads[3] = new Thread(() -> insertionSort());
            threads[4] = new Thread(() -> quickSort(quickLeft, quickRight));
            threads[5] = new Thread(() -> gnomeSort());

            threads[6] = new Thread(() -> selectionSort());
            threads[7] = new Thread(() -> mergeSort());
            threads[8] = new Thread(() -> bubbleSort());
            repaintThread = new Thread(() -> repaintMethod());

            threads[0].start();
            threads[1].start();
            threads[2].start();
            threads[3].start();
            threads[4].start();
            threads[5].start();
            threads[6].start();
            threads[7].start();
//            threads[8].start();
            repaintThread.start();
            mixFlag = true;
        }
    }

    private void repaintMethod() {
        while (true) {
            if (atomicInteger.get() >= 1) {
                this.repaint();
                atomicInteger.set(0);
            }
            if (!checkIsAliveThreads()) {

                this.repaint();

                break;
            }
        }
    }

    private boolean checkIsAliveThreads() {
        for (int i = 0; i < 9; i++) {
            if (threads[i].isAlive())
                return true;
        }
        return false;
    }

    private void shuffleImages() {
        if (!checkIsAliveThreads() && mixFlag) {
            BufferedImage[] bufferedImages = new BufferedImage[9];
            ImageFragment[] tempImageFragments = new ImageFragment[numGroups];
            int[][] tempImageArray = new int[numGroups][imgWidth / numGroupsOnLine * imgHeight / numGroupsOnLine];
            for (int i = 0; i < 9; i++) {
                bufferedImages[i] = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
                bufferedImages[i].getGraphics().drawImage(imageIcons[i].getImage(), 0, 0, null);
            }
            for (int i = 0; i < numGroups; i++) {
                tempImageFragments[i] = new ImageFragment(i);
                bufferedImages[0].getRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine),
                        imgHeight / numGroupsOnLine * (i % numGroupsOnLine),
                        imgWidth / numGroupsOnLine,
                        imgHeight / numGroupsOnLine,
                        tempImageArray[i],
                        0,
                        imgWidth / numGroupsOnLine);
                tempImageFragments[i].setImagePartArray(tempImageArray[i]);
            }
            for (int i = tempImageFragments.length - 1; i > 1; i--) {
                int j = (int) (Math.random() * i);
                ImageFragment tmp = tempImageFragments[j];
                tempImageFragments[j] = tempImageFragments[i];
                tempImageFragments[i] = tmp;
            }

            for (int k = 0; k < 9; k++) {
                System.arraycopy(tempImageFragments, 0, imageFragments[k], 0, tempImageFragments.length);
                for (int i = 0; i < numGroups; i++) {
                    bufferedImages[k].setRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine),
                            imgHeight / numGroupsOnLine * (i % numGroupsOnLine),
                            imgWidth / numGroupsOnLine,
                            imgHeight / numGroupsOnLine,
                            imageFragments[k][i].getImagePartArray(),
                            0,
                            imgWidth / numGroupsOnLine);
                }
                imageIcons[k].setImage(bufferedImages[k]);
                imageLabels[k] = new JLabel(imageIcons[k]);
            }
            mixFlag = false;
            this.repaint();
        }
    }

    private void bubbleSort() {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[0].getImage(), 0, 0, null);
        int count = 0;
        for (int i = 0; i < numGroups - 1; i++) {
            for (int j = 0; j < numGroups - 1 - i; j++) {
                if (imageFragments[0][j + 1].getIndex() < imageFragments[0][j].getIndex()) {
                    ImageFragment tmp = imageFragments[0][j + 1];
                    imageFragments[0][j + 1] = imageFragments[0][j];
                    imageFragments[0][j] = tmp;


                    //Thread.currentThread();
                    try {
                        Thread.sleep(assemblySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((j + 1) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((j + 1) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[0][j + 1].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * (j / numGroupsOnLine), imgHeight / numGroupsOnLine * (j % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[0][j].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    count++;
                    imageIcons[0].setImage(bufferedImage);
                    imageLabels[0] = new JLabel(imageIcons[0]);
                    atomicInteger.incrementAndGet();
                }
            }
        }
        System.out.println("Bubble: " + count);
    }

    private void shakerSort() {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[1].getImage(), 0, 0, null);
        int count = 0;
        int begin = 0;
        int end = imageFragments[1].length - 1;
        while (begin <= end) {
            for (int i = end; i > begin; i--) {
                if (imageFragments[1][i - 1].getIndex() > imageFragments[1][i].getIndex()) {
                    ImageFragment tmp = imageFragments[1][i - 1];
                    imageFragments[1][i - 1] = imageFragments[1][i];
                    imageFragments[1][i] = tmp;

                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((i - 1) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((i - 1) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[1][i - 1].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine), imgHeight / numGroupsOnLine * (i % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[1][i].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    count++;
                    //Thread.currentThread();
                    try {
                        Thread.sleep(assemblySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    imageIcons[1].setImage(bufferedImage);
                    imageLabels[1] = new JLabel(imageIcons[1]);
                    atomicInteger.incrementAndGet();
                }
            }
            begin++;
            for (int i = begin; i < end; i++) {
                if (imageFragments[1][i].getIndex() > imageFragments[1][i + 1].getIndex()) {
                    ImageFragment tmp = imageFragments[1][i + 1];
                    imageFragments[1][i + 1] = imageFragments[1][i];
                    imageFragments[1][i] = tmp;
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((i + 1) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((i + 1) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[1][i + 1].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine), imgHeight / numGroupsOnLine * (i % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[1][i].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    count++;
                    //Thread.currentThread();
                    try {
                        Thread.sleep(assemblySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    imageIcons[1].setImage(bufferedImage);
                    imageLabels[1] = new JLabel(imageIcons[1]);
                    atomicInteger.incrementAndGet();
                }
            }
            end--;

        }
        System.out.println("Shaker: " + count);
    }

    private void combSort() {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[2].getImage(), 0, 0, null);
        int count = 0;
        final double factor = 1.247;
        double step = imageFragments[2].length - 1;

        while (step >= 1) {
            for (int i = 0; i < imageFragments[2].length - step; i++) {
                if (imageFragments[2][i].getIndex() > imageFragments[2][(int) (i + step)].getIndex()) {
                    ImageFragment tmp = imageFragments[2][(int) (i + step)];
                    imageFragments[2][(int) (i + step)] = imageFragments[2][i];
                    imageFragments[2][i] = tmp;


                    //Thread.currentThread();
                    try {
                        Thread.sleep(assemblySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((int) (i + step) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((int) (i + step) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[2][(int) (i + step)].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine), imgHeight / numGroupsOnLine * (i % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[2][i].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    count++;
                    imageIcons[2].setImage(bufferedImage);
                    imageLabels[2] = new JLabel(imageIcons[2]);
                    atomicInteger.incrementAndGet();
                }
            }
            step /= factor;
        }
        for (int i = 0; i < numGroups - 1; i++) {
            for (int j = 0; j < numGroups - 1 - i; j++) {
                if (imageFragments[2][j + 1].getIndex() < imageFragments[2][j].getIndex()) {
                    ImageFragment tmp = imageFragments[2][j + 1];
                    imageFragments[2][j + 1] = imageFragments[2][j];
                    imageFragments[2][j] = tmp;

                    //Thread.currentThread();
                    try {
                        Thread.sleep(assemblySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((j + 1) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((j + 1) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[2][j + 1].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * (j / numGroupsOnLine), imgHeight / numGroupsOnLine * (j % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[2][j].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    count++;
                    imageIcons[2].setImage(bufferedImage);
                    imageLabels[2] = new JLabel(imageIcons[2]);
                    atomicInteger.incrementAndGet();
                }
            }

        }
        System.out.println("Comb: " + count);
    }

    private void insertionSort() {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[3].getImage(), 0, 0, null);
        int count = 0;
        for (int i = 1; i < numGroups; i++) {
            int j = i;
            while (j > 0 && imageFragments[3][j - 1].getIndex() > imageFragments[3][j].getIndex()) {
                ImageFragment tmp = imageFragments[3][j - 1];
                imageFragments[3][j - 1] = imageFragments[3][j];
                imageFragments[3][j] = tmp;

                //Thread.currentThread();
                try {
                    Thread.sleep(assemblySpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((j - 1) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((j - 1) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[3][j - 1].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                bufferedImage.setRGB(imgWidth / numGroupsOnLine * (j / numGroupsOnLine), imgHeight / numGroupsOnLine * (j % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[3][j].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                count++;
                imageIcons[3].setImage(bufferedImage);
                imageLabels[3] = new JLabel(imageIcons[3]);
                atomicInteger.incrementAndGet();
                j--;
            }
        }
        System.out.println("Insertion: " + count);
    }

    private void quickSort(int left, int right) {
        if (left < right) {
            int q = partition(left, right);
            quickSort(left, q);
            quickSort(q + 1, right);
        }
        if (left == 0 && right == imageFragments[4].length - 1)
            System.out.println("Quick: " + quickCount);
    }

    private int partition(int left, int right) {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[4].getImage(), 0, 0, null);
        int tmp = imageFragments[4][(left + right) / 2].getIndex();
        int i = left;
        int j = right;
        while (i <= j) {
            while (imageFragments[4][i].getIndex() < tmp) {
                i++;
            }
            while (imageFragments[4][j].getIndex() > tmp) {
                j--;
            }
            if (i >= j) {
                break;
            }
            ImageFragment tmpArr = imageFragments[4][i];
            imageFragments[4][i] = imageFragments[4][j];
            imageFragments[4][j] = tmpArr;

            //Thread.currentThread();
            try {
                Thread.sleep(assemblySpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bufferedImage.setRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine), imgHeight / numGroupsOnLine * (i % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[4][i].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
            bufferedImage.setRGB(imgWidth / numGroupsOnLine * (j / numGroupsOnLine), imgHeight / numGroupsOnLine * (j % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[4][j].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
            quickCount++;
            imageIcons[4].setImage(bufferedImage);
            imageLabels[4] = new JLabel(imageIcons[4]);
            atomicInteger.incrementAndGet();
            j--;
        }
        return j;
    }

    private void gnomeSort() {
        int count = 0;
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[5].getImage(), 0, 0, null);
        int i = 1;
        while (i < imageFragments[5].length) {
            if (imageFragments[5][i - 1].getIndex() <= imageFragments[5][i].getIndex()) {
                i++;
            } else {
                ImageFragment tmp = imageFragments[5][i - 1];
                imageFragments[5][i - 1] = imageFragments[5][i];
                imageFragments[5][i] = tmp;
                //Thread.currentThread();
                try {
                    Thread.sleep(assemblySpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((i - 1) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((i - 1) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[5][i - 1].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                bufferedImage.setRGB(imgWidth / numGroupsOnLine * (i / numGroupsOnLine), imgHeight / numGroupsOnLine * (i % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[5][i].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                count++;
                imageIcons[5].setImage(bufferedImage);
                imageLabels[5] = new JLabel(imageIcons[5]);
                atomicInteger.incrementAndGet();
                if (i > 1) {
                    i--;
                }
            }
        }
        System.out.println("Gnome: " + count);
    }

    private void selectionSort() {
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[6].getImage(), 0, 0, null);
        int count = 0;
        int minIndex;
        for (int i = 0; i < imageFragments[6].length - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < imageFragments[6].length; j++) {
                if (imageFragments[6][j].getIndex() < imageFragments[6][minIndex].getIndex()) {
                    ImageFragment tmp = imageFragments[6][minIndex];
                    imageFragments[6][minIndex] = imageFragments[6][j];
                    imageFragments[6][j] = tmp;
                    //Thread.currentThread();
                    try {
                        Thread.sleep(assemblySpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * ((minIndex) / numGroupsOnLine), imgHeight / numGroupsOnLine * ((minIndex) % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[6][minIndex].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    bufferedImage.setRGB(imgWidth / numGroupsOnLine * (j / numGroupsOnLine), imgHeight / numGroupsOnLine * (j % numGroupsOnLine), imgWidth / numGroupsOnLine, imgHeight / numGroupsOnLine, imageFragments[6][j].getImagePartArray(), 0, imgWidth / numGroupsOnLine);
                    count++;
                    imageIcons[6].setImage(bufferedImage);
                    imageLabels[6] = new JLabel(imageIcons[6]);
                    atomicInteger.incrementAndGet();
                }
            }
        }
        System.out.println("Selection: " + count);
    }

    private void mergeSort(){
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(imageIcons[7].getImage(), 0, 0, null);
        int count = 0;
    }
}
