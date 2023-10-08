import pandas as pd
import matplotlib.pyplot as plt

# Read the CSV file into a pandas DataFrame
file_path = 'load_test_logs_aws_go_3.csv'  # Replace this with the actual path to your CSV file
# file_path = 'load_test_logs_aws_tomcat_3.csv'
df = pd.read_csv(file_path, delimiter=',')
print(df.columns)

# df['timestamp'] = pd.to_datetime(df['timestamp']).astype(int) // 10**6

# # Print the first few rows of the DataFrame to verify the data is loaded correctly
# print(df.head())

min_start_time = min(df['timestamp'])

print(min_start_time)
df['timestamp'] = df['timestamp'] - min_start_time
print(df.head())

# # Calculate mean, median, p99, min, and max response time
mean_response_time = df['latency'].mean()
median_response_time = df['latency'].median()
p99_response_time = df['latency'].quantile(0.99)
min_response_time = df['latency'].min()
max_response_time = df['latency'].max()

# # Print the calculated statistics
print(f'Mean Response Time: {mean_response_time} milliseconds')
print(f'Median Response Time: {median_response_time} milliseconds')
print(f'P99 Response Time: {p99_response_time} milliseconds')
print(f'Min Response Time: {min_response_time} milliseconds')
print(f'Max Response Time: {max_response_time} milliseconds')

# # Plotting the data (assuming you want to plot latency over time)
plt.figure(figsize=(10, 6))
plt.scatter(df['timestamp'], df['latency'], marker='o', color='b')
plt.xlabel('Timestamp')
plt.ylabel('Latency (milliseconds)')
plt.title('Latency Over Time')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()