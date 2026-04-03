import { Smartphone, Download, BookOpen, CheckCircle2 } from "lucide-react";
import { motion } from "motion/react";

export default function App() {
  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-6 font-sans">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-2xl w-full bg-white rounded-3xl shadow-xl overflow-hidden border border-slate-200"
      >
        <div className="bg-indigo-600 p-8 text-white">
          <div className="flex items-center gap-3 mb-4">
            <Smartphone className="w-8 h-8" />
            <h1 className="text-2xl font-bold tracking-tight">X Note</h1>
          </div>
          <p className="text-indigo-100 text-lg">
            Your production-ready Android application project has been updated to X Note with a sleek black theme and side navigation.
          </p>
        </div>

        <div className="p-8 space-y-8">
          <section>
            <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-4 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-green-500" />
              New Features
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {[
                "Black Theme (Dark Mode)",
                "Side Navigation Drawer",
                "Create Note Modal",
                "Custom Toolbar with Actions",
                "Saved Animation",
                "XNotes Workspace Branding"
              ].map((feature, i) => (
                <div key={i} className="flex items-center gap-2 text-slate-700 bg-slate-50 p-3 rounded-xl border border-slate-100">
                  <div className="w-1.5 h-1.5 rounded-full bg-indigo-500" />
                  {feature}
                </div>
              ))}
            </div>
          </section>

          <section className="bg-indigo-50 rounded-2xl p-6 border border-indigo-100">
            <h2 className="text-indigo-900 font-semibold mb-3 flex items-center gap-2">
              <BookOpen className="w-5 h-5" />
              How to use this project
            </h2>
            <ol className="space-y-3 text-indigo-800 text-sm list-decimal list-inside">
              <li>Open <strong>Android Studio</strong> on your computer.</li>
              <li>Create a new project or open an existing one.</li>
              <li>Copy the generated files from the file explorer on the left into your local project structure.</li>
              <li>Sync Gradle and build the project.</li>
              <li>Run the app on an emulator or a physical Android device.</li>
            </ol>
          </section>

          <div className="flex flex-col gap-4 pt-4">
            <div className="text-xs text-slate-400 text-center italic">
              Note: This is a native Android project. The preview above is for documentation purposes only.
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
