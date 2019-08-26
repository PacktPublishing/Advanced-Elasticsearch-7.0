from etl import etl
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from mpl_toolkits.mplot3d import Axes3D

points  = etl()
Kmean = KMeans(n_clusters=2)
Kmean.fit(points)
labels = Kmean.labels_

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

for item,label in zip(points, labels):
  if label == 0:
    ax.scatter(item[0], item[1], item[2], c='r', marker='o')
  else:
    ax.scatter(item[0], item[1], item[2], c='b', marker='s')

ax.set_xlabel('changeOverTime')
ax.set_ylabel('changePercent')
ax.set_zlabel('volume')
plt.show()
