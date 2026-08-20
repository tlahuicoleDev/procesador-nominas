/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/nomina/:path*",
        destination: "http://localhost:8080/api/nomina/:path*",
      },
    ];
  },
};

export default nextConfig;