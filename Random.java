����   7 X  application/Random  java/lang/Object FIRST_NAMES [Ljava/lang/String; 
LAST_NAMES <clinit> ()V Code  java/lang/String  Alex  Bella  Charlie  David  Emma  Frank  Grace  Henry	       Smith " Johnson $ Williams & Jones ( Brown * Davis , Miller	  .   LineNumberTable LocalVariableTable <init>
  3 1 	 this Lapplication/Random; generateRandomName ()Ljava/lang/String; 9 java/util/Random
 8 3
 8 < = > nextInt (I)I @ java/lang/StringBuilder
  B C D valueOf &(Ljava/lang/Object;)Ljava/lang/String;
 ? F 1 G (Ljava/lang/String;)V I  
 ? K L M append -(Ljava/lang/String;)Ljava/lang/StringBuilder;
 ? O P 7 toString random Ljava/util/Random; 	firstName Ljava/lang/String; lastName 
SourceFile Random.java !                   	  
   �      _� YSYSYSYSYSYSYSYS� � YSY!SY#SY%SY'SY)SY+S� -�    /   
     2  0       1 	  
   /     *� 2�    /        0        4 5   	 6 7  
   �     :� 8Y� :K� *� �� ;2L� -*� -�� ;2M� ?Y+� A� EH� J,� J� N�    /       	  
   "  0       2 Q R    % S T  "  U T   V    W