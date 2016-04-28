# DataClusteringMethods
  A collection of Data Clustering Methods... All the algorythems were created by me for educational purposes. I tried to make them as efficient and generic as possible but they might not suit your application. You are free to use them though
# Algorithms
## K Means
Treats every oberservation as a d dimensional Vector. It generates clusters by assigning each observation to the cluster closest to its corresponding mean. And then recalculates the means simply by calculating the average positions of all vectors in the cluster. The first means are randomly selected point (In this implementation there is an algorythem that keeps the selected points within a certain distance from the average of all oberservations and also tries to keep the selcted means wihin this distance from each other. This algorythem was created to imporve results and performance it is still WIP though)
