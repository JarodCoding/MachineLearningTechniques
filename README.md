# DataClusteringMethods
  A collection of Data Clustering Methods... All the algorythems were created by me for educational purposes. I tried to make them as efficient and generic as possible but they might not suit your application. You are free to use them though
# Algorythems
## K Means
Treats every oberservation as a d dimensional Vector. It generates clusters by assigning each observation to the cluster closest to its corresponding mean. And then recalculates the means simply by calculating the average positions of all vectors in the cluster. The first means are randomly selected point (in this implementation I tried to improve results by writing an algorythem that keeps the selected points within a certain distance from the average of all oberservations and other means this is still WIP though)
