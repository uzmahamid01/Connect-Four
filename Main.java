����   7 i  application/Main  javafx/application/Application <init> ()V Code
  	   LineNumberTable LocalVariableTable this Lapplication/Main; start (Ljavafx/stage/Stage;)V  javafx/fxml/FXMLLoader
    java/lang/Object   getClass ()Ljava/lang/Class;  
game3.fxml
    java/lang/Class   getResource "(Ljava/lang/String;)Ljava/net/URL;
  !  " (Ljava/net/URL;)V
  $ % & load ()Ljava/lang/Object; ( javafx/scene/layout/AnchorPane * javafx/scene/Scene
 ) ,  - (Ljavafx/scene/Parent;)V / application/SampleController
 . 	
  2 3 4 setController (Ljava/lang/Object;)V
 ) 6 7 8 getStylesheets %()Ljavafx/collections/ObservableList; : application.css
 < > = java/net/URL ? @ toExternalForm ()Ljava/lang/String; B D C !javafx/collections/ObservableList E F add (Ljava/lang/Object;)Z
 H J I javafx/stage/Stage K L setScene (Ljavafx/scene/Scene;)V
 H N O  show
 Q S R java/lang/Exception T  printStackTrace primaryStage Ljavafx/stage/Stage; loader Ljavafx/fxml/FXMLLoader; root  Ljavafx/scene/layout/AnchorPane; scene Ljavafx/scene/Scene; e Ljava/lang/Exception; StackMapTable main ([Ljava/lang/String;)V
  c d a launch args [Ljava/lang/String; 
SourceFile 	Main.java !               /     *� �    
       
                    �     X� Y*� � �  M,� #� 'N� )Y-� +:,� .Y� 0� 1� 5*� 9� � ;� A W+� G+� M� M,� P�    O R Q  
   * 
        #  .  E  K  O  S  W      >    X       X U V   > W X   6 Y Z  # , [ \  S  ] ^  _   	 � R Q 	 ` a     3     *� b�    
   
    $  %         e f    g    h