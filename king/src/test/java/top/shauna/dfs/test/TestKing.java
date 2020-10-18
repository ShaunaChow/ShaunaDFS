package top.shauna.dfs.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Test;
import top.shauna.dfs.interact.soldier.KingHeartBeatStarter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 14:46
 * @E-Mail z1023778132@icloud.com
 */
public class TestKing {

    @Test
    public void test5() throws IOException {
        KingHeartBeatStarter kingHeartBeatStarter = new KingHeartBeatStarter();
        kingHeartBeatStarter.onStart();
        System.in.read();
    }

    @Test
    public void test2() throws Exception {
        byte[] image = {1,2,3,4,5,6,7,8,9};
        DataInputStream imageInput = new DataInputStream(new ByteArrayInputStream(image));
        while (imageInput.available()>0){
            System.out.println(imageInput.available());
            imageInput.readByte();
        }
    }

    @Test
    public void test1() throws IOException {
        PriorityQueue<TEST> priorityQueue = priorityQueue = new PriorityQueue<>((o1,o2)-> o1.getAll()>o2.getAll()?1:-1);
        TEST t1 = new TEST(1, 2);
        TEST t2 = new TEST(5, 2);
        TEST t3 = new TEST(2, 2);
        TEST t4 = new TEST(8, 2);
        priorityQueue.offer(t3);
        priorityQueue.offer(t4);
        priorityQueue.offer(t1);
        priorityQueue.offer(t2);
        t1.setF1(100);
        priorityQueue.offer(t1);
        TEST poll = priorityQueue.poll();
        System.out.println(poll);

    }

    @AllArgsConstructor
    @ToString
    @Setter
    @Getter
    class TEST implements Comparable<TEST> {
        float f1;
        float f2;
        float getAll(){
            return f1+f2;
        }
        @Override
        public int compareTo(TEST o) {
            return (int)(this.getAll()-o.getAll());
        }
    }
}
