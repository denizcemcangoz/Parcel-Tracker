// DestinationSorter.java (Elle LinkedList ile)
package structures;

import model.Parcel;

public class DestinationSorter {

    private class CityNode {
        String cityName;
        LinkedList parcelList;
        CityNode left, right;
        int height;

        CityNode(String cityName) {
            this.cityName = cityName;
            this.parcelList = new LinkedList();
            this.left = this.right = null;
            this.height = 1;
        }
    }

    private CityNode root;
    private int nodeCount;

    // Get height of node
    private int height(CityNode node) {
        return node == null ? 0 : node.height;
    }

    // Get balance factor
    private int getBalance(CityNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    // Right rotate
    private CityNode rightRotate(CityNode y) {
        CityNode x = y.left;
        CityNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Left rotate
    private CityNode leftRotate(CityNode x) {
        CityNode y = x.right;
        CityNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    public void insertParcel(Parcel parcel) {
        root = insertParcel(root, parcel);
    }

    private CityNode insertParcel(CityNode node, Parcel parcel) {
        if (node == null) {
            nodeCount++;
            CityNode newNode = new CityNode(parcel.getDestinationCity());
            newNode.parcelList.add(parcel);
            return newNode;
        }

        int cmp = parcel.getDestinationCity().compareTo(node.cityName);
        if (cmp < 0) {
            node.left = insertParcel(node.left, parcel);
        } else if (cmp > 0) {
            node.right = insertParcel(node.right, parcel);
        } else {
            node.parcelList.add(parcel);
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && parcel.getDestinationCity().compareTo(node.left.cityName) < 0) {
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && parcel.getDestinationCity().compareTo(node.right.cityName) > 0) {
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && parcel.getDestinationCity().compareTo(node.left.cityName) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && parcel.getDestinationCity().compareTo(node.right.cityName) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public LinkedList getCityParcels(String city) {
        CityNode node = findNode(root, city);
        return node != null ? node.parcelList : null;
    }

    private CityNode findNode(CityNode node, String city) {
        if (node == null) {
            return null;
        }

        int cmp = city.compareTo(node.cityName);
        if (cmp < 0) {
            return findNode(node.left, city);
        } else if (cmp > 0) {
            return findNode(node.right, city);
        } else {
            return node;
        }
    }

    public Parcel removeParcel(String city, String parcelID) {
        CityNode node = findNode(root, city);
        if (node == null) {
            return null;
        }

        LinkedList.Node parcelNode = node.parcelList.search(parcelID);
        if (parcelNode != null) {
            Parcel parcel = parcelNode.getValue();
            node.parcelList.remove(parcel);
            return parcel;
        }
        return null;
    }

    public int countCityParcels(String city) {
        CityNode node = findNode(root, city);
        return node != null ? node.parcelList.size() : 0;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(CityNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }

    public void inOrderTraversal() {
        inOrderTraversal(root);
    }

    private void inOrderTraversal(CityNode node) {
        if (node != null) {
            inOrderTraversal(node.left);
            System.out.println("City: " + node.cityName + " - Parcels: " + node.parcelList.size());
            inOrderTraversal(node.right);
        }
    }

    public void visualizeBST() {
        System.out.println("\nDestination Sorter (BST):");
        visualizeBST(root, 0);
    }

    private void visualizeBST(CityNode node, int level) {
        if (node != null) {
            visualizeBST(node.right, level + 1);
            for (int i = 0; i < level; i++) System.out.print("    ");
            System.out.printf("%s (%d parcels)\n", node.cityName, node.parcelList.size());
            visualizeBST(node.left, level + 1);
        }
    }


}
