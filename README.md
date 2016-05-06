# Machine Learning Techniqus
  A collection of machine learning techniques. All the algorythems were created by me for educational purposes. I tried to make them as efficient and generic as possible but they might not suit your application. You are free to use them though
# Clustering
Divides data into groups without previous information/classification
## K Means
Treats every oberservation as a d dimensional Vector. The algorythem generates clusters by assigning each observation to the cluster closest to its corresponding mean. And then recalculates the means simply by calculating the average positions of all vectors in the cluster. The first means are randomly selected point (In this implementation there is an algorythem that keeps the selected points within a certain distance from the average of all oberservations and also tries to keep the selcted means wihin this distance from each other. This algorythem was created to imporve results and performance it is still WIP though)
# Classification
Divides data into groups based on previously trained classifications
## KNN
Treats every oberservation as a d dimensional Vector. The training phase simply consists of supplying oberservations linked to classifications. When the algorythem is called to qualify x k nearest neighbours of x are selected. The final classification of x is calculated based on the classification assigned to these neighbours. The simplist way of achieving this is simply assining x the classification with the largest quantity of neighbours. This method can lead to quite unsatisfactory results though especially in case of the traing samples being unevenly distrivbuted among the classification and therefore I made the function which assigns a classification to x based on its k neighbours abstract. I have provided a class which impliments this function which selects the classification as eloborated above but you are free the impliment a differnt solution. I also provided the option of letting the algorithem automatically select k for you in that case the squareroot of the totalamount of trained data is used.

