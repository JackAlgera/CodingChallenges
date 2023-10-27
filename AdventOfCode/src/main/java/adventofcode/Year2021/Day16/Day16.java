package adventofcode.Year2021.Day16;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Day16 {

    private static final String INPUT_NAME = "Year2021/Day16/input.txt";

    public static void main(String[] args) throws IOException {
        new Day16Code().run();
    }

    private static class Day16Code {
        public void run() throws IOException {
            System.out.println("-------- Day 16 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            String input = br.readLine();

            StringBuilder bits = new StringBuilder();

            for (int i = 0; i < input.length(); i++) {
                bits.append(hexToBinary(input.charAt(i)));
            }

            String bitsInput = bits.toString();

            Packet rootPacket = extractPackets(bitsInput);

            System.out.println(bits);
            System.out.println(rootPacket);
            System.out.println("Total version : " + getTotalVersion(rootPacket));
            System.out.println("Value expression : " + calculateExpression(rootPacket));
        }

        private String hexToBinary(char h) {
            return switch (h) {
                case '0' -> "0000";
                case '1' -> "0001";
                case '2' -> "0010";
                case '3' -> "0011";
                case '4' -> "0100";
                case '5' -> "0101";
                case '6' -> "0110";
                case '7' -> "0111";
                case '8' -> "1000";
                case '9' -> "1001";
                case 'A' -> "1010";
                case 'B' -> "1011";
                case 'C' -> "1100";
                case 'D' -> "1101";
                case 'E' -> "1110";
                case 'F' -> "1111";
                default -> throw new IllegalStateException("Incorrect hexadecimal value ?");
            };
        }

        public Packet extractPackets(String packetBits) {
            int version = Integer.parseInt(packetBits.substring(0, 3), 2);
            int typeId = Integer.parseInt(packetBits.substring(3, 6), 2);
            int i = 6;

            switch (typeId) {
                case 4: // literal value
                    StringBuilder binaryValue = new StringBuilder();
                    Packet literalPacket = new Packet(version, typeId, 0L, null);
                    boolean endOfLiteralPacket = false;

                    while (i < packetBits.length() && !endOfLiteralPacket) {
                        endOfLiteralPacket = ("" + packetBits.charAt(i)).equals("0");
                        i++;

                        binaryValue.append(packetBits, i, i + 4);
                        i += 4;
                    }

                    literalPacket.setValue((new BigInteger(binaryValue.toString(), 2)).longValue());
                    literalPacket.setRemainingInput(packetBits.substring(i));
                    return literalPacket;

                default: // Operator
                    int lengthTypeId = ("" + packetBits.charAt(i)).equals("0") ? 15 : 11;
                    i++;
                    Packet operatorPacket = new Packet(version, typeId, null, new ArrayList<>());
                    boolean endOfOperatorPacket = false;
                    String subPacketStr = "";

                    switch (lengthTypeId) {
                        case 15:
                            int lengthSubPackets = Integer.parseInt(packetBits.substring(i, i + lengthTypeId), 2);
                            i += lengthTypeId;
                            subPacketStr = packetBits.substring(i, i + lengthSubPackets);

                            while (!endOfOperatorPacket) {
                                Packet subPacket = extractPackets(subPacketStr);
                                operatorPacket.addSubPacket(subPacket);

                                if (subPacket.getRemainingInput().isBlank() || subPacket.getRemainingInput().matches("^0+?$")) {
                                    endOfOperatorPacket = true;
                                } else {
                                    subPacketStr = subPacket.getRemainingInput();
                                }
                            }
                            operatorPacket.setRemainingInput(packetBits.substring(i + lengthSubPackets));
                            break;
                        case 11:
                            int nbrSubPackets = Integer.parseInt(packetBits.substring(i, i + lengthTypeId), 2);
                            i += lengthTypeId;
                            subPacketStr = packetBits.substring(i);
                            int j = 0;

                            while (j < nbrSubPackets && !endOfOperatorPacket) {
                                Packet subPacket = extractPackets(subPacketStr);
                                operatorPacket.addSubPacket(subPacket);

                                if (subPacket.getRemainingInput().isBlank() || subPacket.getRemainingInput().matches("^0+?$")) {
                                    endOfOperatorPacket = true;
                                }
                                subPacketStr = subPacket.getRemainingInput();
                                j++;
                            }
                            operatorPacket.setRemainingInput(subPacketStr);
                            break;
                    }

                    return operatorPacket;
            }
        }

        public int getTotalVersion(Packet root) {
            int totVersion = 0;

            Queue<Packet> packets = new LinkedList<>();
            packets.add(root);

            while (!packets.isEmpty()) {
                Packet currentPacket = packets.poll();

                if (currentPacket.getSubPackets() != null) {
                    packets.addAll(currentPacket.getSubPackets());
                }

                totVersion += currentPacket.getVersion();
            }

            return totVersion;
        }

        public long calculateExpression(Packet packet) {
            switch (packet.getTypeId()) {
                case 0: // sum
                    long sum = 0;
                    for (Packet subPacket : packet.getSubPackets()) {
                        sum += calculateExpression(subPacket);
                    }
                    return sum;

                case 1: // product
                    long prod = 1;
                    for (Packet subPacket : packet.getSubPackets()) {
                        prod *= calculateExpression(subPacket);
                    }
                    return prod;

                case 2: // min value
                    long minVal = Long.MAX_VALUE;
                    for (Packet subPacket : packet.getSubPackets()) {
                        long val = calculateExpression(subPacket);
                        if (val < minVal) {
                            minVal = val;
                        }
                    }
                    return minVal;

                case 3: // max value
                    long maxVal = Long.MIN_VALUE;
                    for (Packet subPacket : packet.getSubPackets()) {
                        long val = calculateExpression(subPacket);
                        if (val > maxVal) {
                            maxVal = val;
                        }
                    }
                    return maxVal;

                case 4:
                    return packet.getValue();

                case 5:
                    return calculateExpression(packet.getSubPackets().get(0)) > calculateExpression(packet.getSubPackets().get(1)) ? 1 : 0;

                case 6:
                    return calculateExpression(packet.getSubPackets().get(0)) < calculateExpression(packet.getSubPackets().get(1)) ? 1 : 0;

                case 7:
                    return calculateExpression(packet.getSubPackets().get(0)) == calculateExpression(packet.getSubPackets().get(1)) ? 1 : 0;
                default:
                    throw new IllegalArgumentException("Yo something is wrong with the input (or the code ?) !");
            }
        }
    }
//673042774263 - too low
    private static class Packet {
        int version;
        int typeId;
        String remainingInput;
        Long value;
        List<Packet> subPackets;

        public Packet(int version, int typeId, Long value, List<Packet> subPackets) {
            this.version = version;
            this.typeId = typeId;
            this.value = value;
            this.subPackets = subPackets;
            this.remainingInput = "";
        }

        public int getVersion() {
            return version;
        }

        public int getTypeId() {
            return typeId;
        }

        @Override
        public String toString() {
            return version + ", " + typeId + (value != null ? ", " + value : "") + (subPackets != null ? ", (" + subPackets + ")": "");
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public void addSubPacket(Packet subPacket) {
            this.subPackets.add(subPacket);
        }

        public String getRemainingInput() {
            return remainingInput;
        }

        public void setRemainingInput(String remainingInput) {
            this.remainingInput = remainingInput;
        }

        public List<Packet> getSubPackets() {
            return subPackets;
        }
    }
}
