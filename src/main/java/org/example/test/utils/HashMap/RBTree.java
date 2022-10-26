package org.example.test.utils.HashMap;

import lombok.Data;

import java.util.TreeMap;

/**
 * 红黑树五点性质：
 * 1.每个结点不是红色就是黑色。
 * 2.根结点是黑色的。
 * 3.如果一个结点是红色的，则它的两个孩子结点是黑色的。
 * 4.对于每个结点，从该结点到其所有后代叶子结点的简单路径上，均包含相同数目的黑色结点。
 * 5.每个叶子结点都是黑色的（此处的叶子结点指定是空结点）。
 *
 * 删除插入情况分析：https://blog.csdn.net/weixin_40037053/article/details/89947885
 * @param <D>
 */
public class RBTree<D extends Comparable<D>> {

    /**
     * 根节点
     */
    private RBNode<D>  root;

    /**
     * 颜色
     */
    private static final boolean red = true;
    private static final boolean black = false;

    /**
     * 节点定义
     */
    @Data
    private static class RBNode<D extends Comparable<D>>{
        private Boolean color;
        private D data;
        private RBNode<D> parent;
        private RBNode<D>  leftChild;
        private RBNode<D>  rightChild;

        public RBNode(Boolean color, D data, RBNode<D> parent, RBNode<D> leftChild, RBNode<D> rightChild) {
            this.color = color;
            this.data = data;
            this.parent = parent;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        @Override
        public String toString() {
            return "RBNode{" +
                    "color=" + color +
                    ", data=" + data +
                    ", parent=" + parent.data +
                    ", leftChild=" + leftChild.data +
                    ", rightChild=" + rightChild.data +
                    '}';
        }
    }

    /**
     * 插入
     */
    public void insertNode(D data){
        RBNode<D> father = null;
        RBNode<D> next = this.root;

        int compare;
        while(next!=null){
            compare = data.compareTo(next.data);
            father = next;
            if(compare == 0) return;
            else if(compare < 0) next = next.leftChild;
            else next = next.rightChild;
        }
        /*
          默认插入红色节点
          如果插入黑色结点，一定破坏红黑树的性质4，必须对红黑树进行调整。
          如果插入红色结点，可能破坏红黑树的性质3，可能对红黑树进行调整。
         */
        RBNode<D> node = new RBNode<D>(red,data,null,null,null);
        node.parent = father;
        //插入节点
        if(father!=null){
            compare = data.compareTo(father.data);
            if(compare < 0)  father.leftChild = node;
            else father.rightChild = node;
        }
        else {//如果父亲为空说明是空树，则新节点为根，并且设置颜色为黑色。
            node.color = black;
            this.root = node;
        }

        //调整红黑树以符合条件
        fixAfterInsert(node);
    }

    /**
     * 删除
     */
    public void delete(D data){
        RBNode<D> tmp = getNode(data);// 获取删除节点
        if(tmp == null){
            System.out.println("找不到该数据");
            return;
        }
        if(tmp.leftChild!=null && tmp.rightChild!=null){// 删除节点有两个子节点的情况 交换继承节点以变成只有一个子节点或者零个子节点的情况
            //找继承者 习惯用后继结点 故找右子树的最左节点
            RBNode<D> t = tmp.rightChild;
            while(t.leftChild!=null){
                t = t.leftChild;
            }
            //交换继承节点和后继结点
            tmp.data = t.data;
            tmp = t;
        }

        RBNode<D> replaceNode = (tmp.leftChild!=null? tmp.leftChild:tmp.rightChild);// 获取替代节点 只有一个节点则就为该节点，否则没有替代结点。

        if(replaceNode!=null){// 替换节点有一个子节点
            replaceNode.parent = tmp.parent;
            if(tmp.parent == null){// tmp为根节点
                root = replaceNode;
            }
            else if(tmp == tmp.parent.leftChild){
                tmp.parent.leftChild = replaceNode;
            }
            else if(tmp == tmp.parent.rightChild){
                tmp.parent.rightChild = replaceNode;
            }

            if(tmp.color == black){// 如果删除的结点为黑色结点则执行自平衡修复 红色删除后还是平衡的。
                fixAfterDelete(replaceNode);
            }
        }
        else if(tmp.parent == null){// 替代节点为空且父节点为空则为根节点的情况，
            root = null;
        }
        else{// 删除结点为叶子节点即无左右结点且不为根结点
            if(tmp.color == black){// 删除结点为黑则进行自平衡。
                fixAfterDelete(tmp);
            }
            if(tmp.parent!=null){
                if(tmp.parent.leftChild == tmp){
                    tmp.parent.leftChild = null;
                }
                else if(tmp.parent.rightChild == tmp){
                    tmp.parent.rightChild = null;
                }
                tmp.parent = null;
            }
        }

    }

    /**
     * 查找data节点
     * @param data 数据
     * @return 节点
     */
    private RBNode<D> getNode(D data){
        if(this.root == null){
            System.out.println("红黑树为空");
            return null;
        }
        RBNode<D> temp = this.root;
        int com;
        while(temp != null && (com = temp.data.compareTo(data))!=0){
            if(com>0) temp = temp.leftChild;
            else temp = temp.rightChild;
        }
        return temp;
    }

    /**
     * 左旋
     * 过程：父亲下沉为右孩子的左节点，右孩子上升，右孩子的左节点变成父亲的右节点
     */
    private void leftRotate(RBNode<D> node){
        RBNode<D> temp = node.rightChild;
        if(temp.leftChild!=null){
            temp.leftChild.parent = node;
        }
        temp.parent = node.parent;
        node.rightChild = temp.leftChild;
        temp.leftChild = node;
        if(node.parent!=null){// 更新父节点数据
            if(node.parent.leftChild==node){
                node.parent.leftChild = temp;
            }
            else{
                node.parent.rightChild = temp;
            }
        }
        else{// 如果不存在父节点说明为根节点，更新根节点数据
            this.root = temp;
        }
        node.parent = temp;
    }

    /**
     * 右旋
     * 过程：父亲下沉为左孩子的右节点，左孩子上升，左孩子的右节点变成父亲的右节点
     */
    private void rightRotate(RBNode<D> node){
        RBNode<D> temp = node.leftChild;
        if(temp.rightChild!=null){
            temp.rightChild.parent = node;
        }
        temp.parent = node.parent;
        node.leftChild = temp.rightChild;
        temp.rightChild = node;
        if(node.parent!=null){
            if(node.parent.leftChild == node){
                node.parent.leftChild = temp;
            }
            else{
                node.parent.rightChild = temp;
            }
        }
        else{
            this.root = temp;
        }
        node.parent = temp;
    }

    /**
     * 插入后平衡
     */
    private void fixAfterInsert(RBNode<D> node){
        if(node == this.root) return ;
        RBNode<D> father ,grandfather,uncle;
        while((father = node.parent) != null && father.color == red && node!=root){
            grandfather = father.parent;// 获得祖父节点，节点为红色则一定不是根节点则一定存在父节点。
            if(grandfather.leftChild == father){// 父亲节点为左节点的情况
                uncle = grandfather.rightChild;
                if(uncle!=null && uncle.color == red){ // 叔父为红 则叔父变黑，祖父变红
                    father.color = black;
                    uncle.color = black;
                    grandfather.color = red;
                    node = grandfather;
                }
                else{// 叔父为黑，父亲为红
                    if(father.rightChild == node){// 当前节点为右孩子则父亲左旋。
                        leftRotate(father);
                        RBNode<D> temp = node;
                        node = father;
                        father = temp;
                    }
                    father.color = black;
                    grandfather.color = red;
                    rightRotate(grandfather);
                }
            }
            else{// 父亲节点为右节点的情况 与左节点的情况的处理方式刚好相反
                uncle = grandfather.leftChild;
                if(uncle!=null && uncle.color == red){
                    father.color = black;
                    uncle.color = black;
                    grandfather.color = red;
                    node = grandfather;
                }
                else{
                    if(father.leftChild == node){
                        rightRotate(father);
                        RBNode<D> temp = node;
                        node = father;
                        father = temp;
                    }
                    father.color = black;
                    grandfather.color = red;
                    leftRotate(grandfather);
                }
            }
        }

        if(node == root){
            node.color = black;
        }
    }

    /**
     * 删除后平衡
     */
    private void fixAfterDelete(RBNode<D> node){
        RBNode<D> p = node.parent;
        while(node != root && getColor(node) == black){
            if(node == getLeft(p)){// 进入删除情景2.1：替换结点是其父结点的左子结点
                RBNode<D> s = getRight(p);// 兄弟节点S
                RBNode<D> s_left = getLeft(s);
                RBNode<D> s_right = getRight(s);

                if(getColor(s) == red){// 进入删除情景2.1.1：替换结点的兄弟结点是红结点
                    setColor(s,black);
                    setColor(p,red);
                    leftRotate(p);
                    // 得到删除情景2.1.2.3 旋转过后重新分配兄弟结点
                    s = p.rightChild;
                    s_left = getLeft(s);
                    s_right = getRight(s);
                }
                // 经过上面处理 兄弟结点一定为黑 进入删除情景2.1.2：替换结点的兄弟结点是黑结点
                if(getColor(s_left) == black && getColor(s_right) == black){// 进入删除情景2.1.2.3：替换结点的兄弟结点的子结点都为黑结点
                    setColor(s,red);
                    node = p;// 继承父节点继续向上修复
                    p = node.parent;
                }
                else {
                    if(getColor(s_right) == black){// 进入删除情景2.1.2.2：替换结点的兄弟结点的右子结点为黑结点，左子结点为红结点
                        setColor(s_left,black);
                        setColor(s,red);
                        rightRotate(s);
                        s = getRight(p);// 更新s
                        s_left = getLeft(s);
                        s_right = getRight(s);
                    }
                    // 进入删除情景2.1.2.1：替换结点的兄弟结点的右子结点是红结点，左子结点任意颜色
                    setColor(s,getColor(p));
                    setColor(p,black);
                    setColor(s_right,black);
                    leftRotate(p);
                    node = root;
                }
            }
            else{// 右结点的情况 与上面相反
                RBNode<D> s = getLeft(p);
                RBNode<D> s_left = getLeft(s);
                RBNode<D> s_right = getRight(s);

                if(getColor(s) == red){
                    setColor(s,black);
                    setColor(p,red);
                    rightRotate(p);
                    s = getLeft(p);
                    s_left = getLeft(s);
                    s_right = getRight(s);
                }

                if(getColor(s_left) == black && getColor(s_right) == black){
                    setColor(s,red);
                    node = p;
                }
                else{
                    if(getColor(s_left) == black){
                        setColor(s_right,black);
                        setColor(s,red);
                        leftRotate(s);
                        s= getLeft(p);
                        s_left = getLeft(s);
                        s_right = getRight(s);
                    }
                    setColor(s,getColor(p));
                    setColor(p,black);
                    setColor(s_left,black);
                    rightRotate(p);
                    node = root;
                }
            }
        }

        setColor(node,black);
    }

    /**
     * 输出红黑树
     */
    private int getTreeDepth(RBNode<D> root) {
        return root == null ? 0 : (1 + Math.max(getTreeDepth(root.leftChild), getTreeDepth(root.rightChild)));
    }
    private void writeArray(RBNode<D> currNode, int rowIndex, int columnIndex, String[][] res, int treeDepth) {
        // 保证输入的树不为空
        if (currNode == null) return;
        // 先将当前节点保存到二维数组中
        //res[rowIndex][columnIndex] = String.valueOf(currNode.data);
        if(currNode.color == red) res[rowIndex][columnIndex] =  currNode.data + "R";
        else res[rowIndex][columnIndex] = currNode.data  + "B";
        // 计算当前位于树的第几层
        int currLevel = ((rowIndex + 1) / 2);
        // 若到了最后一层，则返回
        if (currLevel == treeDepth) return;
        // 计算当前行到下一行，每个元素之间的间隔（下一行的列索引与当前元素的列索引之间的间隔）
        int gap = treeDepth - currLevel - 1;

        // 对左儿子进行判断，若有左儿子，则记录相应的"/"与左儿子的值
        if (currNode.leftChild != null) {
            res[rowIndex + 1][columnIndex - gap] = "/";
            writeArray(currNode.leftChild, rowIndex + 2, columnIndex - gap * 2, res, treeDepth);
        }

        // 对右儿子进行判断，若有右儿子，则记录相应的"\"与右儿子的值
        if (currNode.rightChild != null) {
            res[rowIndex + 1][columnIndex + gap] = "\\";
            writeArray(currNode.rightChild, rowIndex + 2, columnIndex + gap * 2, res, treeDepth);
        }
    }
    public void show() {
        if (root == null) System.out.println("EMPTY!");
        // 得到树的深度
        int treeDepth = getTreeDepth(root);

        // 最后一行的宽度为2的（n - 1）次方乘3，再加1
        // 作为整个二维数组的宽度
        int arrayHeight = treeDepth * 2 - 1;
        int arrayWidth = (2 << (treeDepth - 2)) * 3 + 1;
        // 用一个字符串数组来存储每个位置应显示的元素
        String[][] res = new String[arrayHeight][arrayWidth];
        // 对数组进行初始化，默认为一个空格
        for (int i = 0; i < arrayHeight; i ++) {
            for (int j = 0; j < arrayWidth; j ++) {
                res[i][j] = " ";
            }
        }

        // 从根节点开始，递归处理整个树
        // res[0][(arrayWidth + 1)/ 2] = (char)(root.val + '0');
        writeArray(root, 0, arrayWidth/ 2, res, treeDepth);

        // 此时，已经将所有需要显示的元素储存到了二维数组中，将其拼接并打印即可
        for (String[] line: res) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i ++) {
                sb.append(line[i]);
                if (line[i].length() > 1 && i <= line.length - 1) {
                    i += line[i].length() > 4 ? 2: line[i].length() - 1;
                }
            }
            System.out.println(sb.toString());
        }
    }

    private void setColor(RBNode<D> p,boolean c){
        if (p != null)
            p.color = c;
    }
    private boolean getColor(RBNode<D> p){
        if(p == null) return false;
        return p.color;
    }
    private RBNode<D> getLeft(RBNode<D> p){
        if(p == null) return null;
        return p.leftChild;
    }
    private  RBNode<D> getRight(RBNode<D> p){
        if(p == null) return null;
        return p.rightChild;
    }
}