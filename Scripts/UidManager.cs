using UnityEngine;

namespace IntegrationPlugin
{
    public class UidManager : MonoBehaviour
    {
        public static string ReceiveString(string value)
        {
            OSPButton[] buttons = FindObjectsOfType<OSPButton>();
            foreach (OSPButton button in buttons)
            {
                button.SetUserId(value);
            }
            return "UID " + value + " set";
        }
    }
}

