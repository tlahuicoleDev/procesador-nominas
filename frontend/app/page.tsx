"use client";

import { useState } from "react";
import { useDropzone } from "react-dropzone";
import axios from "axios";

// Definimos el tipo de dato que esperamos del backend
type ResumenNomina = {
  tipoNomina: string;
  cveTipo: string;
  totalPercepciones: number;
  totalDeducciones: number;
  neto: number;
};

export default function Home() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [resumen, setResumen] = useState<ResumenNomina[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Configuración del Dropzone
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    accept: { "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": [".xlsx"] },
    maxFiles: 1,
    onDrop: (acceptedFiles) => {
      setFile(acceptedFiles[0]);
      setError(null);
      setResumen(null);
    },
  });

  // Función para enviar el archivo al backend
  const handleUpload = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post("http://localhost:8080/api/nomina/procesar", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setResumen(response.data);
    } catch (err) {
      setError("Error al procesar el archivo. Verifica que sea un Excel válido.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gray-100 p-8 flex flex-col items-center">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Procesador de Nóminas</h1>

      {/* Área de Dropzone */}
      <div
        {...getRootProps()}
        className={`w-full max-w-2xl h-32 border-2 border-dashed rounded-lg flex items-center justify-center cursor-pointer transition-colors ${
          isDragActive ? "border-blue-500 bg-blue-50" : "border-gray-300 bg-white"
        }`}
      >
        <input {...getInputProps()} />
        {file ? (
          <p className="text-green-600 font-medium">Archivo listo: {file.name}</p>
        ) : (
          <p className="text-gray-500">Arrastra tu archivo Excel aquí o haz clic para seleccionarlo</p>
        )}
      </div>

      {/* Botón de Procesar */}
      {file && (
        <button
          onClick={handleUpload}
          disabled={loading}
          className="mt-4 px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "Procesando..." : "Procesar Nómina"}
        </button>
      )}

      {/* Mensaje de Error */}
      {error && <p className="mt-4 text-red-600">{error}</p>}

      {/* Tabla de Resultados */}
      {resumen && resumen.length > 0 && (
        <div className="mt-8 w-full max-w-4xl bg-white shadow-md rounded-lg overflow-hidden">
          <table className="w-full text-left text-sm text-gray-700">
            <thead className="bg-gray-200 text-gray-600 uppercase font-bold">
              <tr>
                <th className="px-6 py-3">Tipo de Nómina</th>
                <th className="px-6 py-3 text-right">Percepciones</th>
                <th className="px-6 py-3 text-right">Deducciones</th>
                <th className="px-6 py-3 text-right">Neto</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {resumen.map((item, index) => (
                <tr key={index} className="hover:bg-gray-50">
                  <td className="px-6 py-4 font-medium">{item.tipoNomina}</td>
                  <td className="px-6 py-4 text-right">${item.totalPercepciones.toFixed(2)}</td>
                  <td className="px-6 py-4 text-right">${item.totalDeducciones.toFixed(2)}</td>
                  <td className={`px-6 py-4 text-right font-bold ${item.neto >= 0 ? "text-green-600" : "text-red-600"}`}>
                    ${item.neto.toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}